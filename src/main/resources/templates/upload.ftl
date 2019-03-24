<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Upload</title>
    <link rel="stylesheet" href="/static/lib/easyUpload/main.css">
    <link rel="stylesheet" href="/static/lib/layui/css/layui.css">
    <style>
        html * {
            margin: 0;
            padding: 0;
        }
        .wrapper {
            padding: 20px;
        }
    </style>
</head>
<body>
    <div class="wrapper">


        <div class="easy-uploader">
            <div class="btn-box">

                <div class="layui-upload-drag" id="test10" onChange="selected()">
                    <i class="layui-icon"></i>
                    <p>点击上传，或将文件拖拽到此处</p>
                </div>

                <form id="uploadForm"  class="btn-select-file btn" >
                        <input type="file" name="file" accept=".mp3,.m4a,.wav,.wma" onChange="selected()"  style="float: left;margin: -6px 0px 0px 5px" />
                </form>
                <br/>
                <span class="btn btn-success btn-upload-file btn primary" onclick="upload()" style="float: left;margin: -6px 0px 0px 5px">
                    <i class="glyphicon glyphicon-upload" style="margin-right:3px;"></i>
                    开始上传
                </span>
                <div id="fileInfo"></div>
                <br/><br/><br/>

            </div>
        </div>


    </div>





    <script>

        /******************************************
         * 选择文件
         */
        function selected() {
            var file = document.getElementById('file').files[0];
            // 检验文件类型             accept: '.mp3,.m4a,.wav,.wma',
            if("audio/wav" == file.type || "audio/x-m4a" == file.type || "audio/mp3" == file.type) {
                $("#fileInfo").html(file.name + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' +
                        Math.round(file.size/1024/1024*100)/100 + 'MB' + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;待上传' +
                        '  <i class="fa fa-meh-o" aria-hidden="true"></i>');
                // 更改上传图标

            } else {
                console.log("不支持该文件格式！")
            }
        }

        var BaseUrl =  window.location.host;
        console.log("文件格式！" + file.type )
        console.log("BaseUrl！" + BaseUrl )

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
                    url : "/upload/voice",
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
                         if (data.success) {
                            $("#fileInfo").html(file.name + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' + size + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;上传成功' + '  <i class="fa fa-smile-o" aria-hidden="true"></i>');
                             console.log("成功！")
                        } else {
                            $("#fileInfo").html(file.name + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' + size + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;上传失败' + '  <i class="fa fa-frown-o" aria-hidden="true"></i>');
                            console.log("错误！")
                        }
                    },
                    error: function () {
                        $("#fileInfo").html(file.name + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' + size + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;上传失败' + '  <i class="fa fa-frown-o" aria-hidden="true"></i>');
                        console.log("上传失败！")
                    }
                });  // ajax 结束
            }
        }



    </script>
</body>
</html>