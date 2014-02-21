/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
function goTo(controller,field,defaultId) {
    var selector = '[name="'+field+'"]';
    if ($(selector).size() == 0) {
        selector = '[name="'+field.replace('.','_')+'"]';
    }
    var id = $(selector).val();
    if (id == '') {
        id = defaultId;
    }
    if (id != 'null') {
        var url = baseUrl + '/' + controller + '/edit/' + id;
        location.href = url;
    }
}
function ajaxCalculateDependents(className,event) {
    var url = baseUrl + '/' + className + '/ajaxCalculateDependents?' + $('.body form').serialize();
    $.getJSON(url, function(json) {
        for (var field in json) {
            $('#' + field).text(json[field]);
        }
    });
}
function addOneToMany(field,controller) {
    var row = $("#" + field + "_row");
    var tbody = $("#" + field + "_tbody");
    var num = $("#" + field + "_num");
    var i = parseInt(num.val());
    if (isNaN(i)) {
        i = 0;
    }
    num.val(i+1);
    tbody.append(row.html().replace(/###/g,i));
    var url = baseUrl;
    url = url + '/' + controller;
    url = url + '/ajaxAddOneToMany';
    url = url + '?field=' + field;
    url = url + '&' + $('.body form').serialize();
    $.getJSON(url, function(json) {
        console.dir(json);
        for (var prop in json) {
            var id = field + '_' + i + '_' + prop;
            var val = json[prop];
            if (val) {
                if (val['id']) {
                    id = id + '.id';
                    val = val.id;
                }
                // TODO - my guess is that the first one works for text fields and the second for selects and just doing both should work fine
                $('#' + id).val(val);
                $('[name="' + id + '"]').val(val);
            }
        }
        if ($('select.ajaxUpdate',tbody).length == 0) {
            console.log('making it visible');
            $('tr',tbody).removeClass('hidden');
            $('tr:last :input:visible:enabled:first',tbody).focus();
        }
        else {
            console.log('now going to check for selects that need an ajax update');
            $('tr:last select.ajaxUpdate',tbody).each(function(index) {
                var target = this;
                console.log("found a select within a new one to many that needs an ajax update");
                console.log("current value of select is " + $(target).attr('value'));
                var currentValue = $(target).attr('value');
                var url = $(target).prev().attr('value');
                url = baseUrl + '/' + url;
                console.log(target.id + ' added - invoking ' + url);
                url = url + '&' + $('.body form').serialize();
                $.getJSON(url, function(json) {
                    console.dir(json);
                    var options = '';
                    for (var i = 0; i < json.length; i++) {
                        options += '<option value="' + json[i].id + '"';
                        if (json[i].id == currentValue) {
                            console.log('found a match for ' + currentValue);
                            options += ' selected="selected"';
                        }
                        options += '>' + json[i].value + '</option>';
                    }
                    console.log('updating ' + target.id + ' with ' + options);
                    $(target).html(options);
                    console.log('making it visible');
                    $('tr',tbody).removeClass('hidden');
                    $('tr:last :input:visible:enabled:first',tbody).focus();
                });
            });
        }
    });
}

function newManyToOne(field) {
    var target = $("#" + field);
    var target_id = $("#" + field + "_id");
    $("#" + field + "_new_dialog").dialog({
        autoOpen: false,
        width: 500,
        height: 'auto',
        modal: false,
        draggable: true,
        buttons: {
            "Ok": function() {
                $("#" + field + "_new_dialog div.ui-state-error").remove();
                var params = '';
                var dialog = this;
                $("#" + field + "_new_dialog input").each(function (i) {
                    //if (this.value) {
                        if (params != '') {
                            params = params + '&';
                        }
                        params = params + this.name.replace(field + '_new_','') + '=' + this.value;
                    //}
                });
                var url = baseUrl + '/' + field + '/ajaxSave?' + params;
                $.getJSON(url, function(json) {
                    if (json.display) {
                        target.val(json.display);
                        target_id.val(json.id);
                        $(dialog).dialog("close");
                        $(target).focus();
                    }
                    else {
                        $("#" + field + "_new_dialog table").before('<div class="message ui-state-error">' + json.errors + '</div>');
                    }
                });
            },
            "Cancel": function() {
                $(this).dialog("close");
                $("#" + field + "_new_dialog div.ui-state-error").remove();
                $(target).focus();
            }
        }
    });
    $("#" + field + "_new_dialog").css("display", "inherit");
    $("#" + field + "_new_dialog").dialog('open');
    $("#" + field + "_new_dialog :input:visible:enabled:first").val(target.val());
    $("#" + field + "_new_dialog :input:visible:enabled:first").focus();
}
$(document).ready(function(){
    $('select.ajaxUpdate').live('focus',function(event){
        var url = $(event.target).prev().attr('value');
        url = baseUrl + '/' + url;
        console.log(event.target.id + ' clicked - invoking ' + url);
        url = url + '&' + $('.body form').serialize();
        $.getJSON(url, function(json) {
            console.dir(json);
            var options = '';
            for (var i = 0; i < json.length; i++) {
                options += '<option value="' + json[i].id + '">' + json[i].value + '</option>';
            }
            $(event.target).html(options);
        });
        return true;
    });
    $('.calendarField').live('click',function(){
        var target = $(this).attr('id');
        $('#dateTimeDialog').dialog({
            autoOpen: false,
            width: 310,
            height: 'auto',
            modal: false,
            draggable: true,
            buttons: {
                "Ok": function() {
                    var time = dateTime_hour.value + ":" + dateTime_minute.value + " " + dateTime_am_pm.value;
                    var dateObject = $("#datepicker").datepicker('getDate');
                    var date = $.datepicker.formatDate('D, m/d/yy',dateObject);
                    $('#' + target).val(date + ' at ' + time);
                    $(this).dialog("close");
                },
                "Cancel": function() {
                    $(this).dialog("close");
                }
            }
        });
        var dateTimeParts = $('#' + target).val().split(' ');
        if (dateTimeParts.length == 5) {
            var hour = dateTimeParts[3].split(':')[0];
            var min = dateTimeParts[3].split(':')[1];
            var am_pm = dateTimeParts[4];
            var dateParts = dateTimeParts[1].split("/");
            var date = new Date(dateParts[2],parseInt(dateParts[0])-1,dateParts[1]);
            $('#dateTime_hour').val(hour);
            $('#dateTime_minute').val(min);
            $('#dateTime_am_pm').val(am_pm);
            $('#datepicker').datepicker('setDate',date);
        }
        else {
            var now = new Date();
            var hour = now.getHours();
            var am_pm = hour < 12 ? 'AM' : 'PM';
            if (hour > 12) {
                hour = hour - 12;
            }
            else if (hour == 0) {
                hour = 12;
            }
            $('#dateTime_hour').val(hour);
            $('#dateTime_minute').val(0);
            $('#dateTime_am_pm').val(am_pm);
            $('#datepicker').datepicker('setDate',now);
        }
        $("#dateTimeDialog").css("display", "inherit");
        $("#dateTimeDialog").dialog('open');
    });
    $('td a.delete').live('click',function(){
        var table = $(this).parent().parent().parent().parent();
        var count = $('input.oneToManyCount',table);
        var i = parseInt(count.val());
        if (isNaN(i) || i == 0) {
            i = 0;
        }
        else {
            i = i - 1;
        }
        console.log('updated count - ' + i);
        count.val(i);
        $(this).parent().parent().remove();
        var rows = $('tr.oneToMany',table);
        console.log('num rows - ' + rows.length);
        console.dir(rows.get())
        rows.each(function(i){
            $('input,select',this).each(function(){
                $(this).attr('id',$(this).attr('id').replace(/_\d+_/,'_' + i + '_'));
                $(this).attr('name',$(this).attr('name').replace(/_\d+_/,'_' + i + '_'));
            });
        });
    });
    $('#datepicker').datepicker({
        inline: true,
        showButtonPanel: false,
        dateFormat: 'm/d/yy'
    });
    $('.ui-state-default').hover(
        function(){
            $(this).addClass('ui-state-hover');
        },
        function(){
            $(this).removeClass('ui-state-hover');
        }
        );
    $('.list tbody tr').hover(
        function(){
            $(this).addClass('ui-state-hover');
        },
        function(){
            $(this).removeClass('ui-state-hover');
        }
        );
    $('.linkButton').click(function(){
        window.location.assign($(this).val());
    });
    $('.body :input:visible:enabled:first').focus();
    //TODO - reset this to correct count, not 0!!!!!  But I don't remember when I need to do it!  I guess leave it out for now until I run into that problem again
    //$('input.oneToManyCount').val('0');
    $('div.tabs span.ui-icon-plus').click(function(){
        console.log($(this).parent().attr('href'));
        window.location.assign($(this).parent().attr('href') + '/create');
        return false;
    });
});
