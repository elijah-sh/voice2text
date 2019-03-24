<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>layui</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="/static/lib/layui/css/layui.css"  media="all">
    <!-- 注意：如果你直接复制所有代码到本地，上述css路径需要改成你本地的 -->
</head>
<body>



<fieldset class="layui-elem-field layui-field-title" style="margin-top: 30px;">
    <legend>选完文件后不自动上传</legend>
</fieldset>

<div class="layui-upload">
    <button type="button" class="layui-btn layui-btn-normal" id="test8">选择文件</button>
    <button type="button" class="layui-btn" id="test9">开始上传</button>
</div>

<br/><br/><br/>
<fieldset class="layui-elem-field layui-field-title" style="margin-top: 30px;">
    <legend id="fileInfo"> </legend>
</fieldset>

<table class="layui-hide" id="fileData"></table>


<script src="/static/lib/layui/layui.js" charset="utf-8"></script>
<!-- 注意：如果你直接复制所有代码到本地，上述js路径需要改成你本地的 -->
<script>


    layui.use('upload', function() {
        var $ = layui.jquery , upload = layui.upload;

        //选完文件后不自动上传 voice
        upload.render({
            elem: '#test8',
           // url: '/upload/file',
            url: '/upload/voice',
            auto: false,
            multiple: false,
            bindAction: '#test9',
            accept: 'audio', //音频
            data: { fileType: "hello", fileSize: "world" },    //额外传输的参数
            done: function(res){
                console.log(res);
                if(res.code == 200){ //上传成功
                    $("#fileInfo").html('<span style="color: #5FB878;">上传成功</span>');
                }else {
                    $("#fileInfo").html('<span style="color: #FF5722;">上传失败</span>');
                }
            },
            error: function(res){
                $("#fileInfo").html('<span style="color: #FF5722;">上传失败</span>');
            }
        });

    });

    layui.use('table', function(){
        var table = layui.table;
        table.render({
            elem: '#fileData'
            ,url:'/selectVoiceTextFile'
            ,cols: [[
                {field:'id', width:80, title: 'ID', sort: true}
                ,{field:'titleName', width:80, title: '文件名'}
                ,{field:'fileType', width:80, title: '类型', sort: true}
                ,{field:'fileSize', width:80, title: '大小', sort: true}
                ,{field:'createDate', width:80, title: '时间', sort: true}
                ,{field:'exec', width:80, title: '生成'}
                ,{field:'createDate', width:80, title: '下载'}
            ]]
            ,page: true
        });
    });

</script>

</body>
</html>