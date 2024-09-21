<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8"/>
  <title>信息展示页</title>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="icon" type="image/x-icon" href="../img/favicon.ico"/>
  <link rel="stylesheet" type="text/css" href="../css/utils.css">
</head>
<body>
<div class="container">
  <h1>浏览器与会话信息</h1>
  <table>
    <tr>
      <th style="width: 25%">参数名称</th>
      <th style="width: 25%">参数描述</th>
      <th style="width: 50%">参数值</th>
    </tr>
    <%
      String serverInfo = application.getServerInfo();
      out.println("<tr><td>ServerInfo</td><td>服务器信息</td><td>" + serverInfo + "</td></tr>");
      String[] browserInfo = {"navigator.platform`客户端系统信息", "navigator.appName`浏览器类型", "window.screen.width * window.screen.height`屏幕分辨率",
        "window.screen.colorDepth`屏幕颜色深度"};
      for (String item : browserInfo) {
        String[] kv = item.split("`");
        String key = kv[0];
        String desc = kv[1];
        String value;
        if (key.contains(" * ")) {
          value = "<script>document.write(" + key.replace(" * ", " + \"*\" + ") + ");</script>";
        } else {
          value = "<script>document.write(" + key + ");</script>";
        }
        out.println("<tr><td>" + key + "</td><td>" + desc + "</td><td>" + value + "</td></tr>");
      }
      String[] requestInfo = {"User-Agent`浏览器标识", "RemoteAddr`IP地址", "Locale`浏览器语言", "Accept-Language`浏览器支持的语言",
        "Accept-Charset`浏览器支持的字符编码", "Accept`浏览器支持的媒体类型", "Accept-Encoding`浏览器支持的编码类型", "Accept-Content`浏览器支持的MIME类型",
        "Cookie`浏览器Cookie", "Referer`来源页面"};
      for (String item : requestInfo) {
        String[] kv = item.split("`");
        String key = kv[0];
        String desc = kv[1];
        String value = StringUtils.defaultIfBlank(request.getHeader(key), "未提供");
        out.println("<tr><td>" + key + "</td><td>" + desc + "</td><td>" + value + "</td></tr>");
      }
      String sessionId = session.getId();
      out.println("<tr><td>SessionId</td><td>会话ID</td><td>" + sessionId + "</td></tr>");
      String sessionCreateTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(session.getCreationTime()));
      out.println("<tr><td>SessionCreateTime</td><td>会话创建时间</td><td>" + sessionCreateTime + "</td></tr>");
      String sessionLastAccessTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(session.getLastAccessedTime()));
      out.println("<tr><td>SessionLastAccessTime</td><td>会话最后访问时间</td><td>" + sessionLastAccessTime + "</td></tr>");
      String sessionMaxInactiveInterval = String.valueOf(session.getMaxInactiveInterval());
      out.println("<tr><td>SessionMaxInactiveInterval</td><td>会话最大空闲时间</td><td>" + sessionMaxInactiveInterval + "</td></tr>");
    %>
  </table>
  <h1>系统信息</h1>
  <table>
    <tr>
      <th style="width: 25%">参数名称</th>
      <th style="width: 25%">参数描述</th>
      <th style="width: 50%">参数值</th>
    </tr>
    <%
      String[] systemInfo = {"os.name`操作系统", "os.version`操作系统版本", "os.arch`操作系统架构", "java.version`Java版本", "java.vendor`Java供应商",
        "java.home`Java安装目录", "java.io.tmpdir`Java临时目录", "user.name`用户名", "user.timezone`用户时区",
        "user.country`用户国家", "user.language`用户语言", "file.encoding`文件编码", "file.separator`文件分隔符", "path.separator`路径分隔符"};
      for (String item : systemInfo) {
        String[] kv = item.split("`");
        String key = kv[0];
        String desc = kv[1];
        String value = System.getProperty(key);
        out.println("<tr><td>" + key + "</td><td>" + desc + "</td><td>" + value + "</td></tr>");
      }
      String classesPath = this.getClass().getClassLoader().getResource("").getPath();
      out.println("<tr><td>classesPath</td><td>类路径</td><td>" + classesPath + "</td></tr>");
      // 获取tomcat路径
      String tomcatPath = System.getProperty("catalina.home");
      out.println("<tr><td>catalina.home</td><td>Tomcat路径</td><td>" + tomcatPath + "</td></tr>");
    %>
  </table>
</div>
<%--添加一个悬于右下角的回到主页按钮--%>
<div style="position: fixed; right: 10px; bottom: 10px; background-color: #f4f4f4;
        padding: 10px; border-radius: 5px; border: 1px solid #ccc; text-align: center;">
  <a href="../index.html" style="text-decoration: none; color: #000000;">Back to
    Home</a>
</div>
</body>
</html>
