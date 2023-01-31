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

function searchContact(){
	let query = $("#search-input").val();
	if(query == ''){
		$(".search-result").hide();
	}else{
		//sending request to server
		let url = `http://localhost:8080/search/${query}`;
		
		fetch(url).then((response)=>{
			return response.json();
		})
		.then((data) =>{
			let text=`<div class='list-group'>`;
			
			data.forEach((contact) =>{
				text+=`<a href='/user/${contact.cId}/contact' class='list-group-item list-group-item-action'> ${contact.name} </a>`;
			});
			
			text+=`</div>`;
			$(".search-result").html(text);
			$(".search-result").show();
		});
		
		
	}
}

//not able to use array function showing method undefined at console but working fine in snippet
const searchContact1=()=>{
	let query = $("#search-input").val();
	if(query == ''){
		$(".search-result").hide();
	}else{
		//sending request to server
		let url = `http://localhost:8080/search/${query}`;
		
		fetch(url).then((response)=>{
			return response.json();
		})
		.then((data) =>{
			let text=`<div class='list-group'>`;
			
			data.forEach((contact) =>{
				text+=`<a href='/user/${contact.cId}/contact' class='list-group-item list-group-item-action'> ${contact.name} </a>`;
			});
			
			text+=`</div>`;
			$(".search-result").html(text);
			$(".search-result").show();
		});
		
		
	}	
};
