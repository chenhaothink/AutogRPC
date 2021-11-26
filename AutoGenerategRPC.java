import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoGenerategRPC {
    public static final String FILENAME="pos.h"; //原始接口头文件

    public static final String PACKAGE="pos"; //要生成的proto中package
    public static final String SERVICE="SdkInterface"; //要生成的proto中service
    public static final String RETURNVALUE="returnValue"; //要生成的proto中函数返回值，void即为空

    public static void main(String[] args) {
        //test();
        generateFileProto();
        generateFileCPPClient();
        generateFileJavaServer();
    }

    private static void generateFileJavaServer() {
    
    }

    private static void generateFileCPPClient() {
        String using="";
        String methods="";
        ArrayList<Method> mList=getMethods(FILENAME);
        for(Method m:mList) {
            using += "using " + PACKAGE + "::" + m.methodName + "Request;\n" +
                    "using " + PACKAGE + "::" + m.methodName + "Reply;\n";


            String tempRequest="";
            for (Para p : m.inList) {
                tempRequest+="      request.set_"+p.name.toLowerCase()+"("+p.name+");\n"  ;
            }
            String tempReply="";
            for (Para p : m.outList) {
                tempReply+="          std::cout << \""+p.name+"=\" << reply."+p.name.toLowerCase()+"() << std::endl;\n";
            }

            methods += "  "+m.methodLine.replace(";", "") + "{\n" +
            "      // Data we are sending to the server.\n" +
            "      "+m.methodName+"Request request;\n" +

                    tempRequest+

            "      // Container for the data we expect from the server.\n" +
            "      "+m.methodName+"Reply reply;\n" + "\n" +
            "      // Context for the client. It could be used to convey extra information to\n" +
            "      // the server and/or tweak certain RPC behaviors.\n" +
            "      ClientContext context;\n" + "\n" +
            "      // The actual RPC.\n" +
            "      Status status = stub_->"+m.methodName+"(&context, request, &reply);\n" + "\n" +
            "      // Act upon its status.\n" +
            "      if (status.ok()) {\n" +
            "          std::cout << \"returnValue=\" << reply."+RETURNVALUE.toLowerCase()+"() << std::endl;\n" +

                    tempReply+

            "          return reply."+RETURNVALUE.toLowerCase()+"();\n" +
            "      }\n" +
            "      else {\n" +
            "          std::cout << status.error_code() << \": \" << status.error_message() << std::endl;\n" +
            "          return RPC_FAILED;\n" +
            "      }\n" +
            "  }\n\n";
        }

        String contents="using grpc::Channel;\n" +
                "using grpc::ClientContext;\n" +
                "using grpc::Status;\n" +
                "using "+PACKAGE+"::"+SERVICE+";\n"+
                using+"\n\n"+
                "class "+SERVICE+"Client {\n" +
                " public:\n" +
                "  "+SERVICE+"Client(std::shared_ptr<Channel> channel) : stub_("+SERVICE+"::NewStub(channel)) {}\n"+
                methods+"\n"+
                " private:\n" +
                "  std::unique_ptr<Greeter::Stub> stub_;\n" +
                "};\n";                ;
        writeToFile(PACKAGE+".cc",contents,false); //写入文件
    }

    public static void generateFileProto()
    {
        String method="";
        String para="";

        ArrayList<Method> mList=getMethods(FILENAME);
        for(Method m:mList)
        {
            method+="  rpc "+m.methodName+" ("+m.methodName+"Request) returns ("+m.methodName+"Reply) {}\n";

            String tempRequest="";
            int index=1;
            for (Para p : m.inList) {
                tempRequest+="  "+typeTranslate(ParaType.PROTO,p.type) + " " + p.name+" = "+index+";\n";
                index++;
            }
            String tempReply="";
            index=2; //返回值占了1
            for (Para p : m.outList) {
                tempReply+="  "+typeTranslate(ParaType.PROTO,p.type) + " " + p.name+" = "+index+";\n";
                index++;
            }
            para+="message "+m.methodName+"Request {\n" +
                   tempRequest +
                    "}\n";
            para+="message "+m.methodName+"Reply {\n" +
                    "  "+typeTranslate(ParaType.PROTO,m.returnType)+" "+RETURNVALUE+" = 1;\n" +
                    tempReply +
                    "}\n";
        }


        String contents="syntax = \"proto3\";\n" +
                " \n" +
                "option java_multiple_files = true;\n" +
                "option java_package = \"io.grpc.auto."+PACKAGE.toLowerCase()+"\";\n" +
                "option java_outer_classname = \""+SERVICE+"Proto\";\n" +
                "option objc_class_prefix = \""+PACKAGE.toUpperCase()+"\";\n" +
                "package "+PACKAGE+";\n" +
                " \n" +
                "service  "+SERVICE+"{"
                +method +
                "}\n"+ para;
        writeToFile(PACKAGE+".proto",contents,false); //写入文件
    }


    enum ParaType{PROTO, CPP, JAVA,  }
    public static String typeTranslate(ParaType t, String type)
    {
        if(t==ParaType.PROTO)
        {
            return Constant.TYPE_PROTO.get(type);
        }
        else if(t==ParaType.CPP)
        {

        }
        else  if(t==ParaType.JAVA)
        {

        }
        else
        {

        }
        return "";
    }

    static class Constant {
        public static final Map<String, String> TYPE_PROTO = new HashMap<>();
        static {
            TYPE_PROTO.put("byte", "int32"); //c++，proto
            TYPE_PROTO.put("BYTE", "int32");
            TYPE_PROTO.put("WORD", "int32");
            TYPE_PROTO.put("short", "int32");
            TYPE_PROTO.put("int", "int32");
            TYPE_PROTO.put("DWORD", "int32");
            TYPE_PROTO.put("BYTE*", "bytes");
            TYPE_PROTO.put("BYTE[]", "bytes");
            TYPE_PROTO.put("byte[]", "bytes");
            TYPE_PROTO.put("char*", "bytes");
            TYPE_PROTO.put("WORD*", "bytes");
            TYPE_PROTO.put("DWORD*", "bytes");
        }
    }
    public static void test()
    {
        ArrayList<Method> mList=getMethods("pos.h");
        for(Method m:mList)
        {
            System.out.println(m.returnType +" "+m.methodName);
            for (Para p : m.inList) {
                System.out.println(p.type + " " + p.name);
            }
            for (Para p : m.outList) {
                System.out.println(p.type + " " + p.name);
            }
        }
    }
    enum InOut{
        IN,
        OUT,
        INOUT,
    }
    static class Para{
        InOut inOut;
        String type; //参数类型
        String name; //参数名
    }
    static class Method{
        String methodLine;
        String methodName; //函数名
        String returnType; //返回类型
        ArrayList<Para> inList =new ArrayList<>();
        ArrayList<Para> outList =new ArrayList<>();
    }
    public static ArrayList<Method> getMethods(String fileName)
    {
        ArrayList<Method> mList=new ArrayList<>();
        ArrayList<String> arrayList =readFile(fileName);
        for(String line:arrayList)
        {
            mList.add(getMethod(line));
        }
        return mList;
    }

    public static Method getMethod(String line)
    {
        Method method=new Method();
        method.methodLine=line;
        method.methodName= getRegEx(line,"(?<=\\s).*?(?=\\()").trim();
        method.returnType=getRegEx(line,".*?(?=\\s)").trim(); //返回值 函数名前
        String paraStr=getRegEx(line,"(?<=\\().*?(?=\\))").trim(); //()中内容
        String[] paras=paraStr.split(","); //按,分割成单个参数
        if(paras.length==0) //无参数
        {

        }
        else { //有参数
            for (String para : paras) {
                //System.out.println(para.trim());
                if (para.trim().equals("void")) //无参数
                {

                } else {    //有参数
                    String[] part = para.split(" "); //按空格分割 单个参数 IN OUT BYTE bKeyType
                    if(part.length>2) {
                        if (para.contains("*") ) //指针类型
                        {
                            part[part.length - 2] = part[part.length - 2].replace("*", "")+"*"; //类型 指针合并到类型中
                            part[part.length - 1] = part[part.length - 1].replace("*", ""); //参数名
                        }
                        if(para.contains("[") ) //也是指针类型，可能有[]、[2]
                        {
                            part[part.length - 2] = part[part.length - 2].replaceAll("\\[[0-9]*?\\]", "")+"*"; //类型 指针合并到类型中
                            part[part.length - 1] = part[part.length - 1].replaceAll("\\[[0-9]*?\\]", ""); //参数名
                        }
                        Para p=new Para();
                        p.type=part[part.length - 2]; //类型
                        p.name=part[part.length - 1]; //参数名
                        if(para.contains("IN"))
                        {
                            method.inList.add(p);
                        }
                        if(para.contains("OUT"))
                        {
                            method.outList.add(p);
                        }
                    }
                }
            }
        }
        return method;
    }
    /**
     * 功能：
     * @param str 要匹配的字符串
     * @param reg 正则表达式
     * @return 返回“”或1条匹配结果
     */
    public static String getRegEx(String str,String reg)  //返回0或1条匹配结果
    {
        String result="";
        // 创建 Matcher 对象
        Matcher m = Pattern.compile(reg, Pattern.COMMENTS).matcher(str);
        if( m.find() )
        {
            result = m.group();
        }
        return result;
    }

    public static void writeToFile(String pathName,String contents,boolean append)
    {
        try{
            File file =new File(pathName);
            if(!file.exists()){ //if file doesnt exists, then create it
                file.createNewFile();
            }
            FileWriter fileWritter = new FileWriter(file.getName(),append);//true = append file
            fileWritter.write(contents);
            fileWritter.close();
            System.out.println("write Done");
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public static String manualReplaceSpecial(String line)
    {
        line=line.replace("IN TRANSACTION_OPTIONS *pstTransactionOptions","IN BYTE bPINEncKeyType,IN BYTE pbPINEncKeyIndex[2],IN BYTE bWorkingKeyAlg,IN BYTE bPINBlockFormat,IN BYTE bMagTransServiceCodeProcess,IN BYTE pbPINPolicy[2],IN BYTE pbPINLen[2],IN BYTE bMAGTransOnlinePIN");
        return line;
    }
    public static ArrayList<String> readFile(String name) {
        // 使用ArrayList来存储每行读取到的字符串
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            FileReader fr = new FileReader(name);
            BufferedReader bf = new BufferedReader(fr);
            String line;
            // 按行读取字符串
            while ((line = bf.readLine()) != null) {
                line=line.trim(); //去掉首尾空格
                while(line.contains("  ")) { //去掉所有多余空格
                    line = line.replace("  ", " "); //多个空格全合并成一个
                }
                if(!line.equals("") && !line.startsWith("//")) { //去掉空行和注释行
                    line=manualReplaceSpecial(line);//手工替换特殊内容 结构体等
                    arrayList.add(line);
                    //System.out.println(line);
                }
            }
            bf.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList;
    }
}
