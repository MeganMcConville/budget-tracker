$(document).ready(function (){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options){
        xhr.setRequestHeader(header, token);
    });
    //on click, hide edit button and enter edit mode
    $("#edit-budget-button").click(function(){
        var editButton = $(this);
        editButton.hide();
        $("#cancel-edits-button").removeClass("hidden");
        $("#save-edits-button").removeClass("hidden");
        $("p").hide();
        $(".edit-input").removeClass("hidden");
    });

    //on click of cancel button, revert to original, put values back
    $("#cancel-edits-button").click(function(){
        $("#cancel-edits-button").addClass("hidden");
        $("#save-edits-button").addClass("hidden");
        $("#edit-budget-button").show();
        $(".edit-input").addClass("hidden");
        //change input values back to original
        $(".edit-input").each(function(){
            $(this).val($(this).attr("data-original-value"));
        });
        $("p").show();
    });
    //on click of save button, save changes and get out of edit mode
    $("#save-edits-button").click(function(){

    });

//whole page closing curlies
});