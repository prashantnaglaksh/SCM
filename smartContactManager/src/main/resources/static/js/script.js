console.log("i am script.js and i am working fine");
const toggleSidebar = () => {
	if($(".sidebar").is(":visible")){
		$(".sidebar").css("display", "none");
		$(".content").css("margin-left", "0%");
		
	}else{
		$(".sidebar").css("display", "block");
		$(".content").css("margin-left", "20%");
	}
};

var input = document.getElementById("email_field");
var timeout = null;

input.addEventListener("keyup", function() {
  clearTimeout(timeout);
  timeout = setTimeout(function() {
	  $.ajax({
		  type: "GET",
		  url: "http://localhost:8080/dummy",
		  success: function(response) {
			  console.log(response);
			  // do something with the response
			  //alert(response);
		  }
	  });

  }, 500);
});
