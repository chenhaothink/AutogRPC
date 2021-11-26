# AutogRPC
​Google gRPC implements some code generation, but it's not automated enough.  What I want is to automatically generate all code from existing C++ interfaces, including proto, C++ Client, JAVA Server, etc. (not including protobuf itself).  

Google gRPC实现了一部分代码成生成，还是不够自动化。我想要的是从现有C++接口，全自动生成所有代码，包括proto，C++ Client，JAVA Server等(不含protobuf本身生成部分)。

方案:
 1、已有C++接口，pos.h文件，比如

DWORD GetVersion(OUT char *pbVersion, IN OUT DWORD *pdwVersionLen, IN BYTE bFlag);
DWORD GetDeviceInfo(OUT BYTE *pbInfo, IN OUT DWORD *pdwInfoLen, IN BYTE bFlag);
DWORD UpdatePosFile(IN BYTE bFileType, IN char *szFilePath, IN DWORD dwFlag);

2、 需要生成代码proto，C++ Client，JAVA Server等(不含protobuf本身生成部分)

准备用java来写生成代码程序。

1）先按行读文件pos.h

2）一个字段一个字段的解析，生成各类代码。

IN参数名及类型
函数名
函数返回值类型，OUT参数名及类型
​
