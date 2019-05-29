$(document).ready(function(){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options){
        xhr.setRequestHeader(header, token);
    });

    $("#edit-first-name-button").click(function(){
        var firstNameEditButton = $(this);
        firstNameEditButton.hide();
        $("#update-success-message").hide();
        $("#first-name-display").hide();
        $("#save-first-name-edit-button").removeClass("hidden");
        $("#first-name-edit-input").removeClass("hidden");
    //first name edit button end curlies
    });

    $("#edit-last-name-button").click(function(){
        var lastNameEditButton = $(this);
        lastNameEditButton.hide();
        $("#update-success-message").hide();
        $("#last-name-display").hide();
        $("#save-last-name-edit-button").removeClass("hidden");
        $("#last-name-edit-input").removeClass("hidden");
    //last name edit button end curlies
    });

    function patchUser(payload, button, displayElement, displayValue){
        $.ajax({
            url: "/users",
            type: "PATCH",
            data: JSON.stringify(payload),
            contentType: "application/json"
        //ajax curlies
        })
        .done(function(){
            $("#update-success-message").show();
            button.siblings("input").addClass("hidden");
            button.addClass("hidden");
            button.removeClass("disabled")
            button.prevAll("p").show();
            button.prev("i").show();
            displayElement.text(displayValue);
        //done curlies
        });
    //patchUser curly
    //todo, need semi-colon?
    }

    $("#save-first-name-edit-button").click(function(){
        var saveFirstNameButton = $(this);
        $("#input-error-message").hide();
        if(!saveFirstNameButton.hasClass("disabled")){
            var firstNameInput = $("#first-name-edit-input");
            var firstName = firstNameInput.val().trim();
            if(firstName.length < 1){
                $("#input-error-message").show();
            }
            else{
                saveFirstNameButton.addClass("disabled");
                var payload = {
                    firstName: firstName
                //payload curlies
                };
                patchUser(payload, saveFirstNameButton, $("#first-name-display"), firstName);
            }
        //if curly
        }
    //first name save button end curlies
    });

    $("#save-last-name-edit-button").click(function(){
        var saveLastNameButton = $(this);
        $("#input-error-message").hide();
        if(!saveLastNameButton.hasClass("disabled")){
            var lastNameInput = $("#last-name-edit-input");
            var lastName = lastNameInput.val().trim();
            if(lastName.length < 1){
                $("#input-error-message").show();
            }
            else{
                saveLastNameButton.addClass("disabled");
                var payload = {
                    lastName: lastName
                //payload curly
                };
                patchUser(payload, saveLastNameButton, $("#last-name-display"), lastName);
            }
        //if curly
        }
    //last name save end curlies
    });


//end curlies
});