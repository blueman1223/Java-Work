### SerialGC:
- 特征：年轻代和老年代都是单线程执行，年轻代标记-复制，老年代标记-清除-整理
- 场景：适用于客户端程序或单核处理机
### ParallelGC:
- 特征：采用多线程执行GC，年轻代标记-复制，老年代标记-清除-整理
- 测试经历：单次GC暂停时间优于SerialGC；
  但在测试程序中其吞吐量不如SerialGC,分析日志发现原因是其GC次数是SerialGC的2倍；
  进一步分析日志发现该GC在处理过程中动态调整了分区大小，使用参数-UseAdaptiveSizePolicy关闭功能，其吞吐量上涨，高于SerialGC。
- 场景：适用于多核处理机及优先吞吐量的程序
###  CMSGC(ParNewGC+ConcMarkSweepGC):
- 特征：ParNewGC为SerialGC的并发版本允许用户线程和GC线程并发执行，主要用于回收新生代堆内存；
  ConcMarkSeepGC用于回收老年代堆内存，使用标记-清除算法，不会进行内存整理，在运行过程中会出现内存碎片化，导致大对象不能顺利晋升的情况，如加上内存整理策略，可能会发生性能抖动。
```
步骤：
- CMS-initial-mark：（会STW）
  标记一下 GC Roots 能直接关联到的对象，速度很快
- CMS-concurrent-mark：（耗时长，与业务线程并发执行）
  进行 GC Root Tracing 的过程
- CMS-concurrent-preclean/CMS-concurrent-abortable-preclean：为下final-mark作准备，减少final-mark时间
- CMS Final Remark：（会STW）
  修正并发标记期间，因用户线程并发运行而导致的之前标记对象的引用发生的改变。
- concurrent-sweep：（耗时长，与业务线程并发执行）
```
- 测试经历：整体吞吐量略优于ParallelGC,经日志分析，GC暂停时间与ParallelGC接近，整体GC时间明显大于ParallelGC；推测在多业务线程非交互式场景下，GC暂停时间会短于ParallelGC但整体吞吐量会比它低。
- 场景：适用于响应时间优先的程序

###  G1GC:
- 特征：允许内存回收线程和业务线程并发执行，CMSGC的重大升级。youg region 标记-复制，old-region 标记整理
- 场景：推测适用于堆内存较大的服务性程序
- 测试经历：压测工具的rps略有增加，可能由于机器性能有限，增加内存、调整压测参数之后看不到效果，压测同时机器响应速度明显变慢，测试无疾而终。
