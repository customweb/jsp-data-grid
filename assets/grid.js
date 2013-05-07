(function($) {
	function AjaxPane(element) {
		this.element = $(element);
	}
	String.prototype.contains = function(it) {
		return this.indexOf(it) != -1;
	};

	AjaxPane.prototype.attachEventHandlers = function() {
		$(this.element).find("a.ajax-event").bind("click", {
			pane : this
		}, function(event) {
			event.data.pane.executeEvent(this.href);
			return false;
		});
		
		$(this.element).find("select.ajax-event").bind("change", function(event) {
			$(this).submit();
		});

		$(this.element).find("form.ajax-event-form").submit({
			pane : this
		}, function(event) {

			var href = window.location.href;
			if (href.contains("?")) {
				href = href.replace(/\?([^?]*)$/i, "?" + $(this).serialize());
			} else {
				href = href + "?" + $(this).serialize();
			}
			event.data.pane.executeEvent(href);
			return false;
		});

	};

	AjaxPane.prototype.executeEvent = function(href) {
		var pane = this;
		this.element.css({
			opacity: 0.5,
		});

		$.ajax({
			url : href,
		}).done(function(data) {
			pane.replace(data);
		});
	};

	AjaxPane.prototype.replace = function(content) {
		var newContentElement = $("#" + this.element.attr('id'), $(content));
		if (newContentElement.length > 0) {
			var newContent = newContentElement.html();
			this.element.find('form').replaceWith(newContent);
			this.element.animate({
				opacity : 1,
				duration: 100, 
			});

			this.attachEventHandlers();
		}
		else {
			window.location.reload();
		}
	};

	$(document).ready(function() {
		
		// Handle ajax
		$(".ajax-pane").each(function() {
			var pane = new AjaxPane(this);
			pane.attachEventHandlers();
		});
	});

})(jQuery);
