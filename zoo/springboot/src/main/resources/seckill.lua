-- 1.参数列表
--1.1.优惠券id
local voucherId=ARGV[1]
--1.2.用户id
local userId=ARGV[2]
--1.3.订单id
local userVoucherId=ARGV[3]

-- 2.数据key
--2.1.库存key
local stockKey='seckill:stock:' .. voucherId
--2.2.用户优惠券key
local userVoucherKey='seckill:userVoucher:' .. voucherId

-- 3.脚本业务
--3.1.判断库存是否充足
local stock = tonumber(redis.call('get', stockKey))
if stock == nil then
    --print("库存获取失败: " .. stockKey)
    return -1
end

if (stock<= 0) then
    --3.2.库存不足，返回1
    return 1
end
--3.3.判断用户是否下单
if(redis.call('sismember',userVoucherKey,userId)==1) then
    --3.4.存在，说明重复下单，返回2
    return 2
end
-- 3.5.扣库存 incrby stockKey -1
redis.call('incrby',stockKey,-1)
-- 3.6.下单(保存)用户 sadd userVoucherKey userId
redis.call('sadd',userVoucherKey,userId)
-- 3.7. 发消息到消息队列中 XADD  stream.orders * k1 v1 k2 v2
-- redis.call('xadd','stream.orders','*', 'userId',userId,'voucherId',voucherId,'id',userVoucherId)