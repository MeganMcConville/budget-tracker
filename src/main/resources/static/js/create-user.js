$(document).ready(function(){
    $("#create-user-form").validate({
        rules: {

            firstName: "required",

            lastName: "required",

            password: {
                required: true,
                minlength: 8
            },

            confirm_password:{
                required: true,
                equalTo: "#password"
            }
        },
        messages: {

            password:{
                minlength: "Your password must be at least 8 characters long"
            },
            confirm_password:{
                equalTo: "Passwords do not match"
            }
        }
    });

    $("#create-user-submit-button").click(function(){
        $("input").each(function(){
            $(this).val($(this).val().trim());
        });
    });
});