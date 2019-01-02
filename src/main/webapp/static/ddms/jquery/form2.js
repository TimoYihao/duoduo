$(function () {
    //查询loading
    var LoadingFun2 = new LoadingFun($('.spinner2'));
    var keyword_form = $('.keyword_form');
    var render_keyword = function (param) {
        var keywordTable = $('#keywordTable');
        var arrData =[];
        arrData.push(param.data);
        keywordTable.bootstrapTable({
            idField: 'id',//主键
            columns: [{
                field: 'goods_name',
                title: '商品信息',
                formatter: function indexFormatter(value, item, index) {//在表格前加上序号
                    var self = item.self?'self':'';
                    var valTd='<div><div class="td_img '+self+'">' +
                        '<img src="'+item.good_img_url+'"></div>' +
                        '<div class="td_url">' +
                        '<a target="_blank" href="'+item.good_url+'" >'+item.good_title+'</a>' +
                        '</div></div>';
                    return valTd
                }
            },{
                field: 'normal_price',
                title: '单价',
                formatter: function indexFormatter(value, item, index) {//在表格前加上序号
                    var valTd='￥'+value;
                    return valTd
                }
            }, {
                field: 'group_price',
                title: '团购价',
                formatter: function indexFormatter(value, item, index) {//在表格前加上序号
                    var valTd='￥'+value;
                    return valTd
                }
            }, {
                field: 'page_order',
                title: '排名',
                formatter: function indexFormatter(value, item, index) {//在表格前加上序号
                    return '第<span class="ran">'+value+'</span>名'
                }
            }, {
                field: 'saler_num',
                title: '销量',
                formatter: function indexFormatter(value, item, index) {//在表格前加上序号
                    return value
                }
            }],
            data: arrData
        });
        keywordTable.find('.self').parents('tr').addClass('active');
        tdStyle(keywordTable);
    };
    //查询成功显示信息
    var parent_result = $('.keyword_result');
    var query_suc_keyword = function(param){
        parent_result.show().find('.result_title').text('');
        render_keyword(param);
    };
    //查询ajax
    var queryKeywordAjax = function(formDataKey){
        parent_result.show().hide();
        $('#keywordTable').bootstrapTable('destroy');
        LoadingFun2.showLoading();
        $.post('/ranking/keyword-search',formDataKey,function (param) {
            LoadingFun2.hideLoading();
            if(param.result*1){
                keepData('keyword_form',$('#keyword_sku').val());
                query_suc_keyword(param);
            }else{
                alertError(param.msg);
                parent_result.show().find('.result_title').text(param.msg);
            }
        }).error(function () {
            LoadingFun2.hideLoading();
            alertError('系统错误，请稍后重试');
            parent_result.show().find('.result_title').text('系统错误，请稍后重试');
        })
    };

    //综合点击
    $('.overall').on('click',function () {
        var menuA = $('.dropdown-menu').find('li>a');
        menuA.removeClass('active');
        $(this).addClass('active');
        $('.dropdown_text').text($(this).text());
    });
    //筛选选项点击
    $('.keyword_btn1').on('click',function () {
        var btnParent = $(this).parents('.item_option');
        var hasActive = $(this).hasClass('active');
        var keywordBtn1 = $('.keyword_btn1');
        var choose_btn = $('.choose_btn');
        var arrChoose = [];
        btnParent.find('.keyword_btn1').removeClass('active');
        if(!hasActive){
            $(this).addClass('active');
        }
        //判断是否有筛选条件

        keywordBtn1.each(function (i,item) {
            if(!$(item).hasClass('active')) {
                arrChoose.push(item);
            }
        });
        if(keywordBtn1.length === arrChoose.length){
            choose_btn.removeClass('active');
        }else{
            choose_btn.addClass('active');
        }
    });
    $('.choose_btn').on('click',function () {
        var icon = $(this).find('i');
        if(icon.hasClass('icon-jiantouxiangshang')){
            icon.removeClass('icon-jiantouxiangshang');
            icon.addClass('icon-jiantouxiangxia');
            $('.choose_box').show();
        }else{
            icon.removeClass('icon-jiantouxiangxia');
            icon.addClass('icon-jiantouxiangshang');
            $('.choose_box').hide();
        }
    });
    //置loading
    var price_wrap = $('.price_wrap');
    price_wrap.find('.btn').button('loading');
    function resetBtn(){
        price_wrap.find('.btn').button('loading');
        price_wrap.find('.btn.active').click();
    }
    //keyword
    $('#keyword_form').on('keyup',function () {
        var keyword_val = $(this).val();
        // $.ajax({
        //     url:'/ranking/hot-words',
        //     type:'post',
        //     data: {keyword:keyword_val},
        //     success:function (param) {
        //
        //     }
        // })
    }).on('blur',function () {
        var keyword_val = $.trim($(this).val());
        if(flagKey){
            if(!!keyword_val&&window.location.pathname.indexOf('/monitor/ranking')===-1){
                resetBtn();
                $.post('/ranking/price-range',{keyword:keyword_val},function (param) {
                    if(param.code*1&&param.data.length){
                        $('.price_wrap').find('.btn').button('reset').delay(0).queue(function () {
                            $(this).dequeue();
                            param.data.forEach(function (item,i) {
                                price_wrap.find('.price').eq(i).text(item);
                            });
                        });
                    }else{
                        resetBtn();
                        alertError(param.msg)
                    }
                }).error(function () {
                    resetBtn();
//                    alertError('系统错误')
                })
            }else{
                resetBtn()
            }
        }
    });

    var flagKey = true;
    $('#keyword_btn').click(function () {
        flagKey = false;
        if(form_btn(keyword_form)){
            flagKey = true;
            var formDataKey = {
                keyword:$('#keyword_form').val(),
                sku:$('#keyword_sku').val(),
                sort:$('.keyword_type .keyword_btn.active').attr('key'),
                filter:$('.price_wrap .keyword_btn1.active').text(),
                is_group:$('.only_btn').hasClass('active')?1:0,
            };
            $('html,body').animate({'scrollTop':400});
            queryKeywordAjax(formDataKey);
        }
    });
});