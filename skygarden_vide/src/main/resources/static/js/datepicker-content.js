$(function() {
	$(".datepicker").datetimepicker({
		dateFormat: 'yy-mm-dd',
		timeFormat: "HH:mm",
		hourGrid: 4,
		minuteGrid: 10,
	});
});

function publishedCheckbox(){
	var schedule_published = $('#schedule_published');
	var schedule_unpublished = $('#schedule_unpublished'); 
	if(document.getElementById('published').checked){
		schedule_published.disabled = false;
		schedule_unpublished.disabled = false;
		schedule_published.datepicker( "option", "disabled", false);
		schedule_unpublished.datepicker( "option", "disabled", false);
		schedule_published.css('background', 'rgb(255, 255, 233)');
		schedule_unpublished.css('background', 'rgb(255, 255, 233)');
	}else{		
		schedule_published.disabled = true;
		schedule_unpublished.disabled = true;
		schedule_published.val('');
		schedule_unpublished.val('');
		schedule_published.datepicker( "option", "disabled", true );
		schedule_unpublished.datepicker( "option", "disabled", true );
		schedule_published.css('background', '#dcdcdc');
		schedule_unpublished.css('background', '#dcdcdc');
	}
}