var clip = null;

/**
 * the clip object
 *
 */
function JahiaClip() {

  this.title = window.document.title;
	this.url = window.document.location;
	this._window = window;
	this.selection = window.getSelection(); // test IE

	this.serverUrl = CJN_SERVER;
	this.serverFormPage = "cms/render/default/en/sites/testnote/page.html";
	this.dataTransport = null;
	this.dataContainer = null;
	
}

/**
 * Configure the hidden transport form.
 *
 */
JahiaClip.prototype.prepareTransport = function() {
	this.dataTransport.action = this.serverUrl + '/' + this.serverFormPage;
	this.dataTransport.method = 'GET';
	this.dataTransport.target = 'cj_frame' || '_top';
	this.dataTransport.enctype = "application/x-www-form-urlencoded";
	this.dataTransport.acceptCharset = "UTF-8";
	this.dataTransport.name = "cj_data_transport";
}


/**
 * Fill the hidden transport form that is used to send data back to Jahia.
 *
 */
JahiaClip.prototype.prepareData = function() {
	
	var dataContainer = this.createElement("div", "cj_cn_submit_data_container", null);
	dataContainer.style.display = 'none';
	
	// OBJECT FORM
	this.dataTransport = this.createElement("form", "cj_cn_submit_data_form", dataContainer);
	this.prepareTransport();

	// AJAXCALL PARAMETER
	this.createHiddenElement("ajaxcall", this.dataTransport, "true");

  // FIELD TITLE
	this.createHiddenElement("title", this.dataTransport, this.title);

	// FIELD URL
	this.createHiddenElement("url", this.dataTransport, this.url);
	
	// FIELD NOTE
	this.createHiddenElement("note", this.dataTransport, this.selection);
	
	// FIELD TYPE
	this.createHiddenElement("type", this.dataTransport, "note");
	
	// FIELD SHARE
	this.createHiddenElement("share", this.dataTransport, "true");

	this.dataContainer = dataContainer;
	
		
	return true;
	
}

/**
 * Send data
 *
 */
JahiaClip.prototype.injectData = function() {
	this.dataTransport.submit();
}


/**
 * Create the final iframe
 *
 */
JahiaClip.prototype.prepareForm = function() {
	if (this.dataContainer == null || this.dataTransport == null ) {
		return false;
	}
	
	
	var elBody = this._window.document.body;
	var divForm = document.getElementById("cjwc");
	if (divForm != null) {
	// call clear method
		divForm.parentNode.removeChild(divForm);		
	}
	
	var displayedForm = this.createElement("div", "cjwc", null, "absolute");
	displayedForm.style.top = "0px";
	displayedForm.style.right = "0px";
	displayedForm.style.zIndex = 111110;
	displayedForm.style.margin = "10px";
	
	displayedForm.innerHTML = '<iframe id="cj_frame" '
			+ 'name="cj_frame" src="'+clip.serverUrl+'/'+this.serverFormPage+'?ajaxcall=true" '
			+ 'frameborder="0" style="width:650px; height:500px; '
			+ 'border:1px; padding:0px; margin:0px"></iframe>';
	
	var displayedClose = this.createElement("div", "cjwc_close", null, "absolute");
	displayedClose.style.top = "0px";
	displayedClose.style.right = "0px";
	displayedClose.style.zIndex = 111111;
	displayedClose.style.margin = "10px";
	displayedClose.innerHTML = '<img src="'+CJN_SERVER+'/javascript/note-close-off.gif" onclick="removeElement(\'cj_frame\');removeElement(\'cjwc_close\');" onmouseover="this.src=\''+CJN_SERVER+'/javascript/note-close-on.gif\'" onmouseout="this.src=\''+CJN_SERVER+'/javascript/note-close-off.gif\'"/>';
	
	elBody.appendChild(displayedForm);
	elBody.appendChild(displayedClose);
	elBody.appendChild(this.dataContainer);

	return true;
}

/**
 * Create an element in the DOM structure
 *
 * @param type 			The type of the element (referenced by the name of the HTML tag. ex: div, form, etc.)
 * @param id 				value of the id attribut of the new element. 
 * @param parent 		DOM parent object of the new element.
 * @param position 	position of the new element if needed (can be omitted). Default value is "relative".
 */
JahiaClip.prototype.createElement = function (type, id, parent, position) {
	
	var el = this._window.document.createElement(type);
	if (parent != null) {
		parent.appendChild(el);
	}
	el.id = id;
	if (position) {	
		el.style.position = position;
	} else {
		el.style.position = "relative";
	}
	return el;
}

/**
 * Create an hidden input field in the DOM structure.
 *
 * @param name 		Name of the new hidden input field. Prefix "hd_fld_" is automatically appended.
 * @param parent 	DOM parent object of the new hidden field.
 * @param value 	Value of the new hidden field.
 */
JahiaClip.prototype.createHiddenElement = function (name, parent, value) {
	
		var el = this.createElement("input", "hd_fld_"+name, parent);
		el.type = "hidden";
		el.name = name;
		el.value = value;
		
}


JahiaClip.prototype.send = function() {
	if (!this.prepareData()) {
		return false;
	}
	if (!this.prepareForm()) {
		return false;
	}
	this.injectData();
	
	return true;
}

// ==================================================
function removeElement(id) {
document.getElementById(id).parentNode.removeChild(document.getElementById(id));
}

function doClip() {

	clip = new JahiaClip();
	
	if (!clip.send()) {
		alert("A problem occured. Please retry in few minutes.");
	}
}
doClip();
