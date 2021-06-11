create table `t_user_info` (
`id` char(50) not null comment '用户ID',
`user_name` varchar(30) comment '用户名称',
primary key (`id`)
)engine=InnoDB  default charset=utf8mb4; 

create table `t_order` (
`id` char(50) not null comment '订单ID',
`create_by` char(50) not null comment '订单创建人',
`goods_id` char(50) comment '商品id',
`goods_num` int comment '商品数量',
`create_time` bigint comment '创建时间',
`pay_time` bigint comment '付款时间',
`delivery_time` bigint comment '发货时间',
`finish_time` bigint comment '订单结束时间',
primary key (`id`)
)engine=InnoDB  default charset=utf8mb4; 

create table `t_goods` (
`id` char(50) not null comment '商品ID',
`goods_name` varchar(50) comment '商品名称',
`unit_price` double comment '商品单价',
`amount` int comment '库存数量',
`create_time` bigint comment '创建（上架）时间',
primary key (`id`)
)engine=InnoDB  default charset=utf8mb4; 