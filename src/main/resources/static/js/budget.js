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
        $("#edit-budget-success-message").hide();
        $("#cancel-edits-button").removeClass("hidden");
        $("#save-edits-button").removeClass("hidden");
        $("p").hide();
        $(".edit-input").removeClass("hidden");
    });

    //on click of cancel button, revert to original, put values back
    $("#cancel-edits-button").click(function(){
        $("#edit-budget-error-message").hide();
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
        $("#save-edits-button").addClass("disabled");
        var payload = [];
        $(".budget-table-data").each(function(){
            var row = $(this);
            var id = row.attr("data-budget-item-id");
            var budgetItem = {
                id: id
            };
            var nameInput = row.find(".name-input");
            if(nameInput.val() !== nameInput.attr("data-original-value")){
                budgetItem.name = nameInput.val();
            }
            var amountInput = row.find(".amount-input");
            if(amountInput.val() !== amountInput.attr("data-original-value")){
                budgetItem.amount = amountInput.val();
            }
            var selectedOption = row.find(".type-input :selected");
            var typeInput = row.find(".type-input");
            if(selectedOption.val() !== typeInput.attr("data-original-value")){
                budgetItem.budgetItemType = selectedOption.val();
            }
            payload.push(budgetItem);
        });
        $.ajax({
            url: "/budget-items",
            type: "PATCH",
            data: JSON.stringify(payload),
            contentType: "application/json"
        })
        .done(function(){
            $("#edit-budget-success-message").show();
            $("#cancel-edits-button").addClass("hidden");
            $("#save-edits-button").addClass("hidden");
            $("#edit-budget-button").show();
            $(".edit-input").addClass("hidden");
            $("p").show();
            //put new values to inputs & p's
            $(".budget-table-data").each(function(){
                var row = $(this);
                var nameDisplay = row.find(".name");
                var nameInput = row.find(".name-input");
                nameDisplay.text(nameInput.val());
                nameInput.attr("data-original-value", nameInput.val());
                var amountDisplay = row.find(".amount");
                var amountInput = row.find(".amount-input");
                amountDisplay.text(amountInput.val());
                amountInput.attr("data-original-value", amountInput.val());
                var typeDisplay = row.find(".item-type");
                var typeInput = row.find(".type-input :selected");
                typeDisplay.text(typeInput.val());
                typeInput.attr("data-original-value", typeInput.val());
            });
        })
        .fail(function(){
            $("#edit-budget-error-message").show();
        })
        .always(function(){
            $("#save-edits-button").removeClass("disabled");
        });
    });

//whole page closing curlies
});