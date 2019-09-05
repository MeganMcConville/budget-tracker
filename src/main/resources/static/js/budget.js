$(document).ready(function (){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options){
        xhr.setRequestHeader(header, token);
    });

    function formatMoney(number){
            var sign = number < 0 ? "-" : "";
            var i = String(parseInt(number = Math.abs(Number(number) || 0).toFixed(2)));
            var j = (j = i.length) > 3 ? j % 3 : 0;
            return sign + (j ? i.substr(0, j) + "," : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + ",") + ("." + Math.abs(number - i).toFixed(2).slice(2));
        };

      $('[data-toggle="tooltip"]').tooltip()

    //on click, hide edit button and enter edit mode
    $("#edit-budget-button").click(function(){
        var editButton = $(this);
        editButton.hide();
        $("#edit-budget-success-message").hide();
        $("#create-entry-success-message").hide();
        $("#create-new-item-success-message").hide();
        $("#create-new-item-button").hide();
        $("#cancel-edits-button").removeClass("hidden");
        $("#save-edits-button").removeClass("hidden");
        $("#budget-items-table p:not(.item-type-text, #month-specific-heading, .entry-data)").hide();
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
        $(".budget-table-data:not(.hidden-to-clone)").each(function(){
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
                var editedAmount = (Math.round(amountInput.val()*100)/100).toFixed(2);
                var moneyFormatAmount = formatMoney(editedAmount);
                amountDisplay.text("$" + moneyFormatAmount);
                amountInput.attr("data-original-value", editedAmount);
                amountInput.val(amountInput.attr("data-original-value"));
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
        $("#create-entry-success-message").hide();
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
            var budgetItemTypeText = typeInput.attr("data-item-type-text");
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
            .done(function(budgetItemId){
                var displayAmount = (Math.round(amount*100)/100).toFixed(2);
                var moneyFormatAmount = formatMoney(amount);
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
                var newRow = $(".hidden-to-clone").first().clone();
                newRow.removeClass("hidden-to-clone");
                newRow.attr("data-budget-item-id", budgetItemId);
                var newItemName = newRow.find(".name");
                newItemName.text(name);
                newItemName.attr("data-budget-item-name", name);
                newItemName.attr("data-original-value", name);
                var newItemAmount = newRow.find(".amount");
                newItemAmount.text("$" + moneyFormatAmount);
                newItemAmount.attr("data-original-value", moneyFormatAmount);
                var newItemType = newRow.find(".item-type-text");
                newItemType.text(budgetItemTypeText);
                newItemType.attr("data-original-value", budgetItemType);
                var newItemNameInput = newRow.find(".name-input");
                newItemNameInput.val(name);
                newItemNameInput.attr("data-original-value", name);
                var newItemAmountInput = newRow.find(".amount-input");
                newItemAmountInput.attr("data-original-value", displayAmount);
                newItemAmountInput.val(newItemAmountInput.attr("data-original-value"));
                var totalSpent = newRow.find(".spent-display");
                totalSpent.text("$0.00");
                var totalRemaining = newRow.find(".remaining-display");
                totalRemaining.text("$" + moneyFormatAmount);
                totalRemaining.addClass("positive-amount");
                var entryCollapse = newRow.find(".entries-display");
                entryCollapse.attr("id", "collapse" + budgetItemId);
                var entryTableIcon = newRow.find(".entry-table-opener");
                entryTableIcon.attr("data-target", "#" + entryCollapse.attr("id"));

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
        $("#create-entry-success-message").hide();
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

    $(document).on("click", ".add-entry-button", function(){
        var addEntryIcon = $(this);
        var entryIconRow = addEntryIcon.parent().parent();
        $(".create-entry-confirmation").attr("data-budget-item-id", entryIconRow.attr("data-budget-item-id"));
        $("#item-name-display").text("Add new entry for " + entryIconRow.find(".name").attr("data-budget-item-name"));
        $(".create-entry-confirmation").attr("data-budget-entry-month", $("#date-display").attr("data-display-month"));
        $(".create-entry-confirmation").attr("data-budget-entry-year", $("#date-display").attr("data-display-year"));
        $("#create-entry-success-message").hide();
    //add entry curlies
    });

    $(".create-entry-confirmation").click(function(){
        if(!$(".create-entry-confirmation").hasClass("disabled")){
            $(".create-entry-confirmation").addClass("disabled");
            $("#edit-budget-error-message").hide();
            $("#entry-created-error-message").hide();
            $("#entry-amount-input").removeClass("error");
            $("#amount-error-message").hide();
            $("#loading-gif").show();
            $("#budget-items-table").addClass("transparent");
            var entryAmountInput = $("#entry-amount-input");
            entryAmountInput.attr("required", true);
            var amount = entryAmountInput.val();
            var entryNotesInput = $("#entry-notes-input");
            var notes = entryNotesInput.val();
            var monthNumber = $(".create-entry-confirmation").attr("data-budget-entry-month");
            var year = $(".create-entry-confirmation").attr("data-budget-entry-year");
            var budgetItemId = $(".create-entry-confirmation").attr("data-budget-item-id");

            var payload = {
                amount: amount,
                monthNumber: monthNumber,
                year: year,
                notes: notes,
                budgetItem:{
                    id: budgetItemId
                }
            };
            if(!entryAmountInput.val()){
                entryAmountInput.addClass("error");
                $("#amount-error-message").show();
                $(".create-entry-confirmation").removeClass("disabled");
            }
            else{
                $.ajax({
                    url:"/budget-entries",
                    type: "POST",
                    data: JSON.stringify(payload),
                    contentType: "application/json"
                })

                .done(function(){
                    $("#create-entry-modal").modal("toggle");
                    $("#entry-amount-input").val("");
                    $("#entry-notes-input").val("");
                    var budgetId = $("#budget-title").attr("data-budget-id");
                    $.ajax({
                        url: "/budgets/" + budgetId,
                        type: "GET",
                        data:{
                            month: monthNumber,
                            year: year,
                            tableOnly: "true"
                        }
                    })
                    .done(function(data){
                        $(".budget-table-data:not(.hidden-to-clone)").remove();
                        $(data).insertAfter(".hidden-to-clone");
                        $("#create-entry-success-message").show();
                    })
                    .fail(function(){
                        $("#entry-created-error-message").show();
                    })
                    .always(function(){
                        $("#loading-gif").hide();
                        $("#budget-items-table").removeClass("transparent");
                    });
                //done curlies
                })
                .fail(function(){
                    $("#edit-budget-error-message").show();
                //fail curlies
                })
                .always(function(){
                    $(".create-entry-confirmation").removeClass("disabled");
                //always curlies
                });
            }
        }
    //create entry confirmation closing
    });

    $(".entry-close").click(function(){
        $("#entry-amount-input").removeClass("error");
        $("#amount-error-message").hide();
        $("#entry-amount-input").val("");
        $("#entry-notes-input").val("");
    });

    $("#calendar-icon").click(function(){
        $("#year-input").val($("#date-display").attr("data-display-year"));
    });

//whole page closing curlies
});