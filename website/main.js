jQuery(function($) {
	var form = $("#mainform");
	var firstField = form.children("#firstName");
	var lastField = form.children("#lastName");
	var ajaxDiv = $(".ajax");
	var statusDiv = $("#status");
	var output = $("table tbody");
	$("#mainform").on('submit', function(e) {
		var firstName = firstField.val();
		var lastName = lastField.val();
		$.getJSON("statgrabber.php?first="+encodeURIComponent(firstName)+"&last="+encodeURIComponent(lastName))
			.done(function(data) {
				if (data.error) {
					statusDiv.html("Failed to get data because "+data.error);
				} else {
					var stat = data.data.stat;
					statusDiv.html("Got data for "+stat.firstName+" "+stat.lastName);
					var attributes = stat.attributes;
					var html = "<tr><th>Attribute</th><th>Coverage</th><th>Most Common</th>";
					html += "<th>Distinguished</th><th>Value Count</th></tr>"
					for (var attr in attributes) {
						var info = attributes[attr];
						html += "<tr><td>"+attr+"</td><td>" + (info.coverage * 100).toFixed(2) + "%</td><td class=\"scroll\">";
						html += (info.confidence * 100).toFixed(2) + "%";
						html += "<ul>"
						var best = info.best;
						for (var i=0;i<best.length;++i) {
							html += "<li>" + best[i] + "</li>";
						}
						html += "</ul></td><td class=\"scroll\"><ul>";
						var dist = info.distinguished;
						for (var i=0;i<dist.length;++i) {
							html += "<li>" + dist[i] + "</li>";
						}
						html += "</ul></td><td>" + info.total +" </td></tr>";
					}
					output.html(html);
				}
			})
			.fail(function(obj, status) {
				statusDiv.html("Failed to load data because "+status);
			});
	});
	ajaxDiv.hide();
	$(document).ajaxSend(function() {
		ajaxDiv.fadeIn();
	}).ajaxStop(function() {
		ajaxDiv.fadeOut();
	});
});