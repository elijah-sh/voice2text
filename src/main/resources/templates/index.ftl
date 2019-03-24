<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>V2T</title>
</head>
<body>

<h2>  Hello Hello Hello ! </h2>
<a href="/upload">访问 上传 接口</a> <br>
<a href="/upload/lay">访问 lay上传 接口</a> <br>

<a href="/file">访问 数据表 接口</a> <br>
<a href="/redirect">访问 redirect 接口</a> <br>
<a href="/exception">访问 Exception 接口</a> <br>
<a href="/addCounter">访问 addCounter 接口</a> <br>
<a href="/counter">访问 counter 接口</a> <br>  <br>
<a href="/customer">访问 customer 接口</a> <br>


1、上传音频 <br/>
2、解析音频 <br/>
3、转换文字 <br/>
4、保存文件 <br/>
5、可以下载 <br/>
<form action="/testWebSocker">
    <input name="提交" type="submit">
</form>
<br/>
  <input name="meg" id="message" value="状态" readonly="readonly"> message

<br/>
<br/>
<br/>
<br/>
<div id="page-content">
    <div class="row" style="margin: 0px 10px 10px 10px;padding-bottom: 8px;border-bottom: 1px solid #e7ecf1">

        <p></p>
        <div class="pull-right col-xs-4">

            <form id="uploadForm"  class="fa fa-search "   style="float: left;margin: -6px 0px 0px 15px">
                <a href="javascript:void(0);" class="file btn">
                        上传文件/>
                    <input type="file" id="file" name="file" onChange="selected()"/>
                </a>
            </form>
            <button class="btn btn-success" onclick="upload()" style="float: left;margin: -6px 0px 0px 5px">
                <i class="glyphicon glyphicon-upload" style="margin-right:3px;"></i>
                    开始上传/>
            </button>

        </div>
        <div class="pull-right col-xs-4">
            <div id="fileInfo" class="pull-right" style="margin-right: 15px;margin-top: 8px;border-top: 1px solid #e7ecf1">
                未选择文件
                <i class="fa fa-frown-o" aria-hidden="true">
                </i>
            </div>
        </div>

    </div>

</body>
</html>

<script type="text/javascript" src="/static/js/jquery.min.js"></script>
 <script type="text/javascript" >


     /******************************************
      * 选择文件
      */
     function selected() {
         var file = document.getElementById('file').files[0];
         // 检验文件类型
         if("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" == file.type || "application/vnd.ms-excel" == file.type) {
             $("#fileInfo").html(file.name + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' +
                     Math.round(file.size/1024/1024*100)/100 + 'MB' + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;待上传' +
                     '  <i class="fa fa-meh-o" aria-hidden="true"></i>');
             // 更改上传图标

         } else {
             kendo.ui.showErrorDialog({
                 title: "错误",
                 message: "不支持该文件格式！"
             })
         }
     }

    var BaseUrl =  window.location.host;

     /******************************************
      * 上传
      */
     function upload() {
         var file = document.getElementById('file').files[0];
         var size = Math.round(file.size/1024/1024*100)/100 + 'MB';   // 把一个数字舍入为最接近的整数
         var fd = new FormData($("#uploadForm")[0]);
         fd.append('fileName', file.name)
         fd.append('fileSize', size);
         fd.append('fileType', 1);

         if(file ) {

             $.ajax({
                 url : BaseUrl + "/upload",
                 type : 'POST',
                 data : fd,
                 // 告诉jQuery不要去处理发送的数据
                 processData : false,
                 // 告诉jQuery不要去设置Content-Type请求头
                 contentType : false,
                 beforeSend:function(){
                     $("#fileInfo").html(file.name + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' + size + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;正在上传' + '  <i class="fa fa-spinner fa-pulse"></i>');
                 },
                 success: function (data) {
                     $('#grid').data('kendoGrid').dataSource.page(1);
                     if (data.success) {
                         $("#fileInfo").html(file.name + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' + size + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;上传成功' + '  <i class="fa fa-smile-o" aria-hidden="true"></i>');
                         kendo.ui.showInfoDialog({
                             title: "成功",
                             message: data.message
                         })
                     } else {
                         $("#fileInfo").html(file.name + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' + size + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;上传失败' + '  <i class="fa fa-frown-o" aria-hidden="true"></i>');
                         kendo.ui.showErrorDialog({
                             title: "错误",
                             message: data.message
                         });
                     }
                 },
                 error: function () {
                     $("#fileInfo").html(file.name + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' + size + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;上传失败' + '  <i class="fa fa-frown-o" aria-hidden="true"></i>');
                     kendo.ui.showErrorDialog({
                         title: "错误",
                         message: "上传失败！"
                     })
                 }
             });  // ajax 结束
         }
     }


     var socket;
     if(typeof(WebSocket) == "undefined") {
         console.log("您的浏览器不支持WebSocket");
     }else{
         var url='ws://'+window.location.host+'/websocket/null';
         console.log("您的浏览器支持WebSocket");
         //实现化WebSocket对象，指定要连接的服务器地址与端口  建立连接
         //等同于socket = new WebSocket("ws://localhost:8083/checkcentersys/websocket/20");
         socket = new WebSocket(url);
         //打开事件
         socket.onopen = function() {
             console.log("Socket 已打开");
             socket.send("这是来自客户端的消息" + location.href + new Date());
         };
         //获得消息事件
         socket.onmessage = function(msg) {
             console.log(msg.data);
             //发现消息进入    开始处理前端触发逻辑
             sendMessage(msg.data);

         };
         //关闭事件
         socket.onclose = function() {
             console.log("Socket已关闭");
         };
         //发生了错误事件
         socket.onerror = function() {
             alert("Socket发生了错误");
         }

     }

     function sendMessage(message) {
         $('#message').val(message);
     }

 </script>
