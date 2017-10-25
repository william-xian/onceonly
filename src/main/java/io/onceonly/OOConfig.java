package io.onceonly;

import io.onceonly.annotation.I18nCfg;
import io.onceonly.annotation.I18nCfgBrief;

@I18nCfg("zh")
public class OOConfig {
	@I18nCfgBrief("保存日志请求的参数的最大长度")
	public static final int REQ_LOG_MAX_BYTES = 8 * 1024;
}
