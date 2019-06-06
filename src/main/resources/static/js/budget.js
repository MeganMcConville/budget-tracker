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
        $("#create-new-item-button").hide();
        $("#cancel-edits-button").removeClass("hidden");
        $("#save-edits-button").removeClass("hidden");
        $("#budget-items-table p").hide();
        $(".edit-input").removeClass("hidden");

    });

    //on click of cancel button, revert to original, put values back
    $("#cancel-edits-button").click(function(){
        $("#edit-budget-error-message").hide();
        $("#cancel-edits-button").addClass("hidden");
        $("#save-edits-button").addClass("hidden");
        $("#edit-budget-button").show();
        $("#create-new-item-button").show();
        $(".edit-input").addClass("hidden");
        //change input values back to original
        $(".edit-input").each(function(){
            $(this).val($(this).attr("data-original-value"));
        });
        $("#budget-items-table p").show();
    });
    //on click of save button, save changes and get out of edit mode
    $("#save-edits-button").click(function(){
        $("#save-edits-button").addClass("disabled");
        $("#edit-budget-error-message").hide();
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
            var selectedOption = row.find(".type-input .active input");
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
            $("#create-new-item-button").show();
            $(".edit-input").addClass("hidden");
            $("#budget-items-table p").show();
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
                var typeInput = row.find(".type-input .active input");
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
    $("#create-new-item-button").click(function(){
        $("#create-new-item-button").hide();
        $("#create-new-item-button").addClass("disabled")
        $("#edit-budget-button").hide();
        $("#edit-budget-error-message").hide();
        $("#edit-budget-success-message").hide();
        $(".new-item-input").removeClass("hidden");
        $(".save-buttons").removeClass("hidden");
    //show create new curlies
    });
//whole page closing curlies
});