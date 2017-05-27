# universal-utils
之前工作时写的工具包，很多工具都没写完，之后会慢慢完善

### 2017年3月7日
　　增加了两个注解Keep和KeepAll，用于在混淆时过滤不需要混淆的类、方法、参数。KeepAll用于过滤整个类，Keep用于精确的过滤类(类本身)/方法/参数。  
　　使用前需要在proguard-rules.pro中加入以下参数：  
	-keep,allowobfuscation @interface com.sunzhk.annotation.Keep  
	-keep,allowobfuscation @interface com.sunzhk.annotation.KeepAll  
	-keep @com.sunzhk.annotation.Keep class *  
	-keepclassmembers class * {  
	　　@com.sunzhk.annotation.Keep *;  
	}  
	-keep @com.sunzhk.annotation.KeepAll class * {*;}  
