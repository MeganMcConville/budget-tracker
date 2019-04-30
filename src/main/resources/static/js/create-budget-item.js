$(document).ready(function (){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options){
        xhr.setRequestHeader(header, token);
    });
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
            var budgetIdInput = $("#budget-id");
            var payload = {
                amount: amount,
                name: name,
                budgetItemType: budgetItemType,
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
                nameInput.val("");
                amountInput.val("");
                $("#budget-item-type .active").removeClass("active");
                var nameElement = $("<p></p>").text(name);
                var amountElement = $("<p></p>").text(amount);
                var typeElement = $("<p></p>").text(budgetItemType);
                var row = $("<div></div>");
                row.append(nameElement, amountElement, typeElement);
                $("#finished-budget-items").append(row);
                $("#column-names").removeClass("hidden");
                $("#column-names").addClass("displaying");
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