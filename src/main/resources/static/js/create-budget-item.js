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

    $(".create-budget-item-button").click(function(){
        var button = $(this);
        if(!button.hasClass("disabled")){
            button.addClass("disabled");
            $("#error-message").hide();
            $("#success-message").hide();
            var nameInput = $("#budget-item-name");
            var name = nameInput.val();
            var amountInput = $("#budget-item-amount");
            var amount = amountInput.val();
            var typeInput = $("#budget-item-type .active input");
            var budgetItemType = typeInput.val();
            var budgetItemTypeText = typeInput.attr("data-type-text-value");
            var budgetIdInput = $("#budget-id");
            var recurring = $("#recurring-checkbox").is(":checked");
            var payload = {
                amount: amount,
                name: name,
                budgetItemType: budgetItemType,
                recurring: recurring,
                budget:{
                    id: budgetIdInput.val()
                }
            };
            $.ajax({
                url: "/budget-items",
                type: "POST",
                data: JSON.stringify(payload),
                contentType: "application/json"
            })
            .done(function(){
                $("#success-message").show();
                var finishedTable = $("#finished-budget-items");
                nameInput.val("");
                amountInput.val("");
                $("#recurring-checkbox").prop("checked", false);
                var displayAmount = formatMoney(amount);
                $("#budget-item-type .active").removeClass("active");
                var nameElement = $("<p></p>").text(name);
                nameElement.addClass("name-display");
                var amountElement = $("<p></p>").text("$" + displayAmount);
                amountElement.addClass("amount-display");
                var typeElement = $("<p></p>").text(budgetItemTypeText);
                typeElement.addClass("type-display");
                var recurringElement = $("<input type='checkbox'>");
                recurringElement.prop("disabled", true);
                recurringElement.prop("checked", recurring);
                var recurringDiv = $("<div></div>");
                recurringDiv.append(recurringElement);
                recurringDiv.addClass("recurring-display");
                var spacingDiv = $("<div></div>");
                spacingDiv.addClass("spacing-div");
                var row = $("<div></div>");
                row.append(nameElement, amountElement, typeElement, recurringDiv, spacingDiv);
                row.addClass("new-item-grid-display");
                row.addClass("new-row-width");
                finishedTable.append(row);
            })
            .fail(function(){
                $("#error-message").show();
            })
            .always(function(){
                $(".create-budget-item-button").removeClass("disabled");
            });
        }
    });
});