$(document).ready(function(){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options){
        xhr.setRequestHeader(header, token);
    });

    $("#budget-select").change(function(){
        $("#no-valid-years-message").hide();
        var budgetId = $(this).val();
        $("#year-select").empty();

        $.ajax({
            url:"/reports/active-years",
            type: "GET",
            data: {budgetId: budgetId},
        })
        .done(function(data){
            //don't populate if the budget is not active, set button to disabled, display error message
            if(data.length < 1){
                $("#no-valid-years-message").show();
                $("#download-report-button").prop("disabled", true);
            }
            else{
                $("#year-select").prop("disabled", false);
                var $yearSelect = $("#year-select");
                $.each(data, function(){
                    $yearSelect.append($("<option />").val(this).text(this));
                });
                $("#download-report-button").prop("disabled", false);
            }
        })
        .fail(function(){
        })
        .always(function(){
        });
    });

});