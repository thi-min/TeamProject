(function ($) {
    'use strict';

    var $window = $(window),
        $document = $(document),
        $html = $('html'),
        $head = $('head'),
        $screen = $.screen,
        $inArray = $.inArray;

    $(function () {

        //�ш린�쒕��� 肄붾뱶 �묒꽦�댁＜�몄슂
        /* �뚯씠釉� 諛섏쓳�� �쒗뵆由� �쒖옉 (25.02.17 異붽�) */

        var $tableResponsive = $('.colgroup #contents').find('.table.responsive');
    
        $tableResponsive.each(function(index, element) {
            var $element = $(element),
                rowdivIs = $element.find('td, th').is('[rowdiv]'),
                theadLength = $element.find('thead').length;
    
            if(rowdivIs == false && !theadLength == 0){
                $element.find('tbody th, tbody td').each(function(index, element) {
                    var $this = $(element),
                        thisIndex = $this.index(),
                        theadText = $this.parents('tbody').siblings('thead').find('th').eq(thisIndex).text();
    
                    $this.attr('data-content', theadText);
                });
    
                $element.find('tfoot th, tfoot td').each(function(index, element) {
                    var $this = $(element),
                        thisIndex = $this.index(),
                        theadText = $this.parents('tfoot').siblings('thead').find('th').eq(thisIndex).text();
    
                    $this.attr('data-content', theadText);
                });
            }
        });
        /* �뚯씠釉� 諛섏쓳�� �쒗뵆由� 醫낅즺 (25.02.17 異붽�) */
        
        /* �щ떕�� 諛뺤뒪 �쒗뵆由� �쒖옉 [25.02.25 異붽�] */
        var $ExpandBox = $('.expand_box'),
            $ExpandWrap = $ExpandBox.find('.expand_wrap'),
            $ExpandBtn = $ExpandBox.find('.expand_btn');

        $('.expand_box .expand_btn').on('click',function(){
            var $this = $(this),
                ThisExpandBox = $this.parents('.expand_box'),
                ActiveBox = ThisExpandBox.hasClass('active');
            if(!ActiveBox){
                $this.attr('title','以꾩뿬蹂닿린')
                $(ThisExpandBox).addClass('active');
            }else{
                $this.attr('title','�쇱쿂蹂닿린')
                $(ThisExpandBox).removeClass('active');
            }
        });
        /* �щ떕�� 諛뺤뒪 �쒗뵆由� �� [25.02.25 異붽�] */
        
        /* 肄섑뀗痢� ��찓�� �쒗뵆由� �쒖옉 [25.02.28] 異붽� */
        /* var $ConTabWrap = $('.con_tab_wrap');
        $ConTabWrap.find('.tab_menu_btn').on('click', function() {
            var $this = $(this),
                index = $this.index(),
                isActive = $this.hasClass('active');

            if (!isActive) {
                $ConTabWrap.find('.tab_menu_item, .tab_menu_btn, .tab_con_item').removeClass('active').removeAttr('title');

                $this.addClass('active').attr('title', '�좏깮��');
                $this.parent().addClass('active');
                
                $ConTabWrap.find('.tab_con_item').eq(index).addClass('active').attr('title', '�좏깮��');
            }
        }); */
        /* 肄섑뀗痢� ��찓�� �쒗뵆由� �� [25.02.28] 異붽� */
        /* �앹뾽 �숈옉 �쒖옉 [25.03.04] */
        var $ConPopBox = $('.con_popup_box');
        //�앹뾽 �닿린
        $ConPopBox.find('.open_popup').on('click',function(){
            var $this = $(this),
            PopItemAct = $this.parent('.temp_btn').next('.popup_box').hasClass('active');
            $this.removeClass('active');
            if(!PopItemAct){
                //�앹뾽 �대━怨� �リ린踰꾪듉 �ъ빱��
                setTimeout(function(){
                    $this.addClass('active');
                    $this.parents('.con_popup_box').find('.pop_close_btn').focus();
                },1);

                $ConPopBox.find('.open_popup, .popup_box').removeClass('active');
                $('body').removeClass('pop_not_scroll');

                $this.parent().next('.popup_box').addClass('active');
                $('.con_popup_box').closest('body').addClass('pop_not_scroll'); //body�� �대옒�� 異붽�(�앹뾽 而ㅽ듉)

                //�앹뾽�대� �띿뒪�� 諛뺤뒪 �ㅽ겕濡� �섏뿀�꾨븣 tabindex 遺���
                var $ThisPopTextBox = $this.parents('.con_popup_box').find('.popup_box .popup_item .pop_text_box'),
                    //css max-height洹몃�濡� 異붿텧�� px源뚯� 洹몃�濡� 媛��몄삤湲� �꾨Ц�� �レ옄 遺�遺� 異붿텧 �� 10吏꾨쾿�쇰줈 蹂�寃�
                    //10吏꾨쾿�쇰줈 蹂�寃쎌쓣 �앸왂�섎㈃ �ㅻⅤ寃� �댁꽍�� �� �덈뒗 媛��μ꽦�� �덉쓬(�� - 8吏꾨쾿 010 泥섎읆)
                    TextBoxMaxHeight = parseInt($ThisPopTextBox.css('max-height'), 10);
                    
                    //pop_text_box�� max-height媛믨낵 �꾩옱媛믪쓣 鍮꾧탳�댁꽌 �꾩옱媛믪씠 max-height媛� �댁긽�대㈃ tabindex='0'異붽�
                    if(TextBoxMaxHeight === $ThisPopTextBox.innerHeight()){
                        $ThisPopTextBox.attr('tabindex','0');
                        /* console.log('max-height:', $ThisPopTextBox.css('max-height')); */ //max-height 泥댄겕
                        /* console.log('innerHeight:', $ThisPopTextBox.innerHeight()); */ //�붿냼�� �꾩옱�믪씠 泥댄겕
                    }else{
                        $ThisPopTextBox.removeAttr('tabindex','0');
                    }
                }
        });
        //�앹뾽 �リ린
        $ConPopBox.find('.popup_box .chk_close_btn, .pop_close_btn').on('click',function(){
            var $this = $(this);

            $('.con_popup_box').find('.open_popup.active').focus();
            $this.closest('.popup_box').removeClass('active');
            $('.con_popup_box').closest('body').removeClass('pop_not_scroll');

            $('.popup_item .pop_text_box').removeAttr('tabindex','0');//tabindex ��젣
        });

        $(document).ready(function() {
            var $popCloseBtn = $('.pop_close_btn'),
                //temp_btn:last-child�� 紐⑤뱺 �먯떇�� 湲곗��쇰줈 �≪� �댁쑀 = input, button, a�쒓렇 紐⑤몢 �명솚 媛��ν븯寃� �섍린 �꾪븿
                $lastBtn = $('.pop_btn_box .temp_btn:last-child *');
        
            //pop_close_btn�먯꽌 �ы봽��+��쓣 媛먯� 留덉�留� �붿냼濡� �ъ빱�� �대룞
            $popCloseBtn.on('keydown', function(e) {
                if (e.keyCode === 9 && e.shiftKey) { 
                    e.preventDefault();
                    $lastBtn.focus();
                }
            });
            //留덉�留� �붿냼�먯꽌 ��쓣 媛먯� pop_close_btn�쇰줈 �ъ빱�� �대룞
            $lastBtn.on('keydown', function(e) {
                if (e.keyCode === 9 && !e.shiftKey) {
                    e.preventDefault();
                    $popCloseBtn.focus();
                }
            });
        });

        /* �앹뾽 �숈옉 �� [25.03.04] */
        /* �꾩퐫�붿뼵 �쒖옉 [25.03.07] */
        /* �� �꾩퐫�붿뼵 */
        var $AccordionBox = $('.accordion_box');
        $AccordionBox.find('.accordion_btn').attr('title','�댁슜 �닿린');

        //媛곴컖�� accordion_item
        $AccordionBox.find('.accordion_item').each(function(){
            var $Boxthis = $(this);
            //媛곴컖�� accordion_item �� accordion_btn
            $Boxthis.find('.accordion_btn').on('click',function(){
            var $this = $(this),
                $AccordionItem = $this.closest('.accordion_item'),
                $AccordionTextBox = $AccordionItem.find('.accordion_text_box'),
                IsActive = $AccordionItem.hasClass('active');

                if(!IsActive){
                    $AccordionTextBox.stop().slideDown(250);
                    $this.attr('title','�댁슜 �リ린');
                    $AccordionItem.addClass('active');
                }else{
                    $AccordionTextBox.stop().slideUp(250);
                    $this.attr('title','�댁슜 �닿린');
                    $AccordionItem.removeClass('active');
                }
            });
        });
        /* �묒� �꾩퐫�붿뼵 */
        var $SmAccordionBox = $('.sm_accordion_box');
        $SmAccordionBox.find('.sm_accordion_btn').attr('title','�댁슜 �닿린');

        //媛곴컖�� accordion_item
        $SmAccordionBox.find('.sm_accordion_item').each(function(){
            var $SmBoxthis = $(this);
            //媛곴컖�� accordion_item �� accordion_btn
            $SmBoxthis.find('.sm_accordion_btn').on('click',function(){
            var $this = $(this),
                $SmAccordionItem = $this.closest('.sm_accordion_item'),
                $SmAccordionTextBox = $SmAccordionItem.find('.sm_accordion_text'),
                SmIsActive = $SmAccordionItem.hasClass('active');

                if(!SmIsActive){
                    $SmAccordionTextBox.stop().slideDown(250);
                    $this.attr('title','�댁슜 �リ린');
                    $SmAccordionItem.addClass('active');
                }else{
                    $SmAccordionTextBox.stop().slideUp(250);
                    $this.attr('title','�댁슜 �닿린');
                    $SmAccordionItem.removeClass('active');
                }
            });
        });
        /* �꾩퐫�붿뼵 �� [25.03.07] */
    });
})(jQuery);