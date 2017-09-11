package cn.mx.app.common;

import io.onceonly.annotation.Const;
import io.onceonly.annotation.I18nConst;

@I18nConst("zh")
public class User {
	@Const(name="苹果IOS系统")
	public static final int OS_IOS = 1;
	@Const(name="Android系统")
	public static final int OS_ANDROID = 2;
	@Const(name="微信")
	public static final int OS_WX = 3;
}
