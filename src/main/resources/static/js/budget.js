$(document).ready(function (){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options){
        xhr.setRequestHeader(header, token);
    });

      $('[data-toggle="tooltip"]').tooltip()

    //on click, hide edit button and enter edit mode
    $("#edit-budget-button").click(function(){
        var editButton = $(this);
        editButton.hide();
        $("#edit-budget-success-message").hide();
        $("#create-new-item-success-message").hide();
        $("#create-new-item-button").hide();
        $("#cancel-edits-button").removeClass("hidden");
        $("#save-edits-button").removeClass("hidden");
        $("#budget-items-table p:not(.item-type-text, #month-specific-heading)").hide();
        $(".edit-input").removeClass("hidden");
        $("#month-specific-heading").removeClass("hidden");
    });

    //on click of cancel button, revert to original, put values back
    $("#cancel-edits-button").click(function(){
        $("#edit-budget-error-message").hide();
        $("#cancel-edits-button").addClass("hidden");
        $("#save-edits-button").addClass("hidden");
        $("#edit-budget-button").show();
        $("#create-new-item-button").show();
        $(".edit-input").addClass("hidden");
        $("#month-specific-heading").addClass("hidden");
        //change input values back to original
        $(".edit-input").each(function(){
            $(this).val($(this).attr("data-original-value"));
        });
        $("#budget-items-table p:not(#month-specific-heading)").show();
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
            var updateMonth = $("#date-display").attr("data-display-month");
            var updateYear = $("#date-display").attr("data-display-year");
            var budgetItemUpdate = {
                budgetItem: budgetItem,
                month: updateMonth,
                year: updateYear
            };
            var nameHasChanged = false;
            var amountHasChanged = false;

            var nameInput = row.find(".name-input");
            if(nameInput.val() !== nameInput.attr("data-original-value")){
                budgetItemUpdate.name = nameInput.val();
                nameHasChanged = true;
            }
            else{
                budgetItemUpdate.name = nameInput.attr("data-original-value");
            }

            var amountInput = row.find(".amount-input");
            if(amountInput.val() !== amountInput.attr("data-original-value")){
                budgetItemUpdate.amount = amountInput.val();
                amountHasChanged = true;
            }
            else{
                budgetItemUpdate.amount = amountInput.attr("data-original-value");
            }
            if($(row.find(".month-specific-checkbox")).is(":checked")){
                budgetItemUpdate.monthSpecific = true;
            }
            if(amountHasChanged || nameHasChanged){
                payload.push(budgetItemUpdate);
            }
        });

        $.ajax({
            url: "/budget-item-updates",
            type: "POST",
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
            $("#month-specific-heading").addClass("hidden");
            $("#budget-items-table p:not(#month-specific-heading)").show();
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
        $("#create-new-item-success-message").hide();
        $(".new-item-container").removeClass("hidden");
        $(".new-item-input").removeClass("hidden");
        $(".save-buttons").removeClass("hidden");
        $("html, body").animate({scrollTop: $("#new-item-div").offset().top}, 1000);
    //show create new curlies
    });

    $("#cancel-new-item-x").click(function(){
        $(".new-item-container").addClass("hidden");
        $(".save-buttons").addClass("hidden");
        $(".new-item-input").addClass("hidden");
        $("#edit-budget-button").show();
        $("#create-new-item-button").removeClass("disabled");
        $("#create-new-item-button").show();
        //clear inputs
        $("#new-item-name-input, #new-item-amount-input").val("");
        $("#new-item-type-input .active").removeClass("active");
    //cancel item button curlies
    });

    $("#save-new-item-check").click(function(){
        if(!$("#save-new-item-check").hasClass("disabled")){
            $("#save-new-item-check").addClass("disabled");
            $("#edit-budget-error-message").hide();
            var nameInput = $("#new-item-name-input");
            var name = nameInput.val();
            var amountInput = $("#new-item-amount-input");
            var amount = amountInput.val();
            var typeInput = $("#new-item-type-input .active input");
            var budgetItemType = typeInput.val();
            var budgetId = $("#new-item-name-input").closest(".row-data").attr("data-budget-id");
            var payload = {
                amount: amount,
                name: name,
                budgetItemType: budgetItemType,
                budget:{
                    id: budgetId
                }
            //payload curly
            };
            $.ajax({
                url:"/budget-items",
                type: "POST",
                data: JSON.stringify(payload),
                contentType: "application/json"
            //ajax curlies
            })
            .done(function(){
                $("#create-new-item-success-message").show();
                $("#new-item-name-input, #new-item-amount-input").val("");
                $("#new-item-type-input .active").removeClass("active");
                $(".new-item-container").addClass("hidden");
                $(".save-buttons").addClass("hidden");
                $(".new-item-input").addClass("hidden");
                $("#edit-budget-button").show();
                $("#create-new-item-button").removeClass("disabled");
                $("#create-new-item-button").show();
                //add new item to budget
                var newRow = $(".budget-table-data").first().clone();
                var newItemName = newRow.find(".name");
                newItemName.text(name);
                newItemName.attr("data-original-value", name);
                var newItemAmount = newRow.find(".amount");
                newItemAmount.text("$" + amount);
                newItemAmount.attr("data-original-value", amount);
                var newItemType = newRow.find(".item-type-text");
                newItemType.text(budgetItemType);
                newItemType.attr("data-original-value", budgetItemType);
                var newItemNameInput = newRow.find(".name-input");
                newItemNameInput.val(name);
                newItemNameInput.attr("data-original-value", name);
                var newItemAmountInput = newRow.find(".amount-input");
                newItemAmountInput.val(amount);
                newItemAmountInput.attr("data-original-value", amount);
                newRow.insertBefore(".new-item-container");
            })
            .fail(function(){
                $("#edit-budget-error-message").show();
            })
            .always(function(){
                $("#save-new-item-check").removeClass("disabled");
            });
        //if curly
        }
    //save new item closing curlies
    });

    $("#rename-budget-button").click(function(){
        $("#rename-budget-button").hide();
        $("#budget-title").hide();
        $("#edit-budget-success-message").hide();
        $("#create-new-item-success-message").hide();
        $("#rename-budget-input").removeClass("hidden");
        $(".rename-buttons").removeClass("hidden");
    //rename button curlies
    });

    $("#rename-budget-x").click(function(){
        $("#edit-budget-error-message").hide();
        $("#budget-title").show();
        $("#rename-budget-error-message").hide();
        $(".rename-buttons").addClass("hidden");
        $("#rename-budget-input").addClass("hidden");
        $("#rename-budget-input").val($("#budget-title").attr("data-original-value"));
        $("#rename-budget-button").show();
    //cancel rename curlies
    });

    $("#rename-budget-check").click(function(){
        $("#rename-budget-error-message").hide();
        $("#edit-budget-error-message").hide();
        var budgetId = $("#budget-title").attr("data-budget-id");
        if(!$("#rename-budget-check").hasClass("disabled")){
            var renameBudgetInput = $("#rename-budget-input");
            var updateBudgetName = renameBudgetInput.val().trim();
            if(updateBudgetName.length < 1){
                $("#rename-budget-error-message").show();
            }
            else{
                $("#rename-budget-check").addClass("disabled");
                var payload = {
                    name: updateBudgetName,
                    id: budgetId
                };
                $.ajax({
                    url:"/budgets",
                    type: "PATCH",
                    data: JSON.stringify(payload),
                    contentType: "application/json"
                })
                .done(function(){
                    $("#update-budget-success-message").show();
                    $("#rename-budget-error-message").hide();
                    $(".rename-buttons").addClass("hidden");
                    $("#rename-budget-input").addClass("hidden");
                    $("#rename-budget-button").show();
                    $("#budget-title").show();
                    $("#budget-title").text(updateBudgetName);
                    $("#budget-title").attr("data-original-value", updateBudgetName);
                    $("#rename-budget-input").val(updateBudgetName);
                })
                .fail(function(){
                    $("#edit-budget-error-message").show();
                })
                .always(function(){
                    $("#rename-budget-check").removeClass("disabled");
                });
            //else curly
            }
        //if curly
        }
    //rename budget curlies
    });
    $(".delete-item-button").click(function(){
        var deleteIcon = $(this);
        $(".delete-confirmation").attr("data-budget-item-id", deleteIcon.parent().parent().attr("data-budget-item-id"))
    //trashcan curlies
    });

    $(".delete-confirmation").click(function(){
        if(!$(".delete-confirmation").hasClass("disabled")){
            $(".delete-confirmation").addClass("disabled");
            $("#edit-budget-error-message").hide();
            var budgetItemId = $(".delete-confirmation").attr("data-budget-item-id");
            $.ajax({
                url: "/budget-items/" + budgetItemId,
                type: "DELETE"
            })
            .done(function(){
                $(".grid-container[data-budget-item-id="+budgetItemId+"]").remove();
                $("#delete-item-modal").modal("toggle");
            })
            .fail(function(){
                $("#edit-budget-error-message").show();
            })
            .always(function(){
                $(".delete-confirmation").removeClass("disabled");
            });
        //if curlies
        }
    //delete confirmation function curlies
    });


//whole page closing curlies
});