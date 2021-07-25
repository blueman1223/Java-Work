## Cluster集群
### 配置
> 配置三个Redis实例7379/8379/9479，准备用作cluster
```shell
port 7379/8379/9379
cluster-enabled yes
cluster-config-file nodes.conf
cluster-node-timeout 5000
appendonly yes
```

### 创建Redis-Cluster
> 注: Redis-cluster 互相通信时会基于各自的ip地址 所以使用docker搭建redis-cluster时必须特别注意网络环境的配置
>
> 在WSL2 - DOCKER环境中 只能在容器里进行Cluster的配置和访问，原因是容器内为虚拟网络环境，与宿主机网络环境存在隔离 ：cluster通讯使用的是容器内网IP，宿主机无法访问这个地址。
> 
> 在一般Docker环境下redis官方推荐使用 -net host模式启动容器，这样容器就能和宿主机共享ip。遗憾的是 -net host模式实际上容器使用的是dockerd进程的网络环境配置，在wsl2-docker的模式下这个地址也是一个虚拟地址
```shell
# 将刚才启动的3个Redis实例用作创建cluster
# replica参数代表从redis实例，这里设置为0，如果需要采取主从模式那么至少需要 x * (replica_num + 1)个Redis实例才能创建成功
$ redis-cli --cluster create 172.17.0.2:7379 172.17.0.3:8379 172.17.0.4:9379 --cluster-replicas 0
>>> Performing hash slots allocation on 3 nodes...
Master[0] -> Slots 0 - 5460
Master[1] -> Slots 5461 - 10922
Master[2] -> Slots 10923 - 16383
M: 5a2d32529521d44acfb14d7c3d6b0c63c8c15dfb 172.17.0.2:7379
   slots:[0-5460] (5461 slots) master
M: 17c107a800da19fa972b14787f9b769751d9b764 172.17.0.3:8379
   slots:[5461-10922] (5462 slots) master
M: b571b418a2368c30e4873b9359cf8f375d80cfad 172.17.0.4:9379
   slots:[10923-16383] (5461 slots) master
Can I set the above configuration? (type 'yes' to accept): yes
>>> Nodes configuration updated
>>> Assign a different config epoch to each node
>>> Sending CLUSTER MEET messages to join the cluster
Waiting for the cluster to join
..
>>> Performing Cluster Check (using node 172.17.0.2:7379)
M: 5a2d32529521d44acfb14d7c3d6b0c63c8c15dfb 172.17.0.2:7379
   slots:[0-5460] (5461 slots) master
M: 17c107a800da19fa972b14787f9b769751d9b764 172.17.0.3:8379
   slots:[5461-10922] (5462 slots) master
M: b571b418a2368c30e4873b9359cf8f375d80cfad 172.17.0.4:9379
   slots:[10923-16383] (5461 slots) master
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.
```

### 测试
```shell
$ redis-cli -c -p 7379
127.0.0.1:7379> set foo bar
-> Redirected to slot [12182] located at 172.17.0.4:9379
OK
```

### 结合Replica
> 可以在创建cluster前配置好实例，在创建时让cluster自动选，也可以在创建完cluster后再添加

### 结合sentinel
> 在sentinel 配置中 monitor所有slot(主节点)就可以了，这里不再重复描述详细过程。