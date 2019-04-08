$(document).ready(function(){
    $("#create-user-form").validate({
        rules: {

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
    })
})