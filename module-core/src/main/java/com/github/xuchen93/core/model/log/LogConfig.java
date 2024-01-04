package com.github.xuchen93.core.model.log;

import lombok.Data;

/**
 * @author xuchen.wang
 * @date 2023/3/10
 */
@Data
public class LogConfig{
	private boolean time = true;
	private boolean thread = true;
	private boolean method = true;
}
