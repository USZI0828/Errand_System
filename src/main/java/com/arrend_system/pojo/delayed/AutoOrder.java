package com.arrend_system.pojo.delayed;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class AutoOrder implements Delayed {

    //订单id
    private Integer id;
    //订单的过期时间(时间戳表示)
    private Long expire;
    //订单的过期时间间隔
    public static final long expireTime = TimeUnit.SECONDS.toMinutes(5);

    public AutoOrder(Integer id, LocalDateTime orderTime) {
        this.id = id;
        Long createTime = orderTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        this.expire = expireTime + createTime;
    }

    @Override
    public long getDelay(@NotNull TimeUnit unit) {
        return unit.convert(expire - System.currentTimeMillis(),TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(@NotNull Delayed o) {
        return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }
}
