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



// first request to server to craete order
function paymentStart(){
	console.log("payment started");
	let amount = $("#payment_field").val();
	if(amount == '' || amount == null){
		alert("Please fill");
		return;
	}
	
	// using AJAX to send request to server o create ORDER
	$.ajax(
		{
			url : "/user/create_order",
			data:JSON.stringify({amount:amount, info:'order_request'}),
			contentType:'application/json',
			type:'POST',
			dataType:'json',
			success:function(response){
				//this function will invoked when success
				console.log(response);
				if(response.status == "created"){
					//open payment form
					var options = {
    "key": "YOUR_KEY_ID", // Enter the Key ID generated from the Dashboard
    "amount": "50000", // Amount is in currency subunits. Default currency is INR. Hence, 50000 refers to 50000 paise
    "currency": "INR",
    "name": "Smart Contact Manager",
    "description": "Donation Transaction",
    "image": "https://example.com/your_logo",
    "order_id": response.id, //This is a sample Order ID. Pass the `id` obtained in the response of Step 1
    "handler": function (response){
        alert(response.razorpay_payment_id);
        alert(response.razorpay_order_id);
        alert(response.razorpay_signature);
        updatePaymentOnServer(response.razorpay_payment_id, response.razorpay_order_id, 'paid');
    },
    "prefill": {
        "name": "",
        "email": "gaurav.kumar@example.com",
        "contact": ""
    },
    "notes": {
        "address": "Razorpay Corporate Office"
    },
    "theme": {
        "color": "#3399cc"
    }
};

var rzp = new Razorpay(options);

rzp.on('payment.failed', function (response){
        alert(response.error.code);
        alert(response.error.description);
        alert(response.error.source);
        alert(response.error.step);
        alert(response.error.reason);
        alert(response.error.metadata.order_id);
        alert(response.error.metadata.payment_id);
});
   
    rzp.open();

					
				}
			},
			error:function(error){
				// invoked when error
				console.log(error);
				alert("Something wentwrong !!");
			}
		}
	);
}

function updatePaymentOnServer(payment_id, order_id, status){
	$.ajax(
		{
			url : "/user/update_order",
			data:JSON.stringify({payment_id:payment_id, order_id:order_id, status:status}),
			contentType:'application/json',
			type:'POST',
			dataType:'json',
			success:function(response){
				alert("paymenet details also upadetd on server as well");
			},
			error:function(error){
				alert("issue while updating payment detaulson the server");
			}
		});
}



