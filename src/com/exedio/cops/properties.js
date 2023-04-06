function onCheckbox(checkbox,inputId)
{
	document.getElementById(inputId).style.display = checkbox.checked ? "block" : "none";
}

function toggleUnspecified(image)
{
	var imageSrc = image.src;
	if(imageSrc.substring(imageSrc.length-8)=="true.png")
	{
		image.src = imageSrc.substring(0, imageSrc.length-8) + "false.png";
		document.getElementById("properties-table").classList.add("hide-unspecified");
	}
	else
	{
		image.src = imageSrc.substring(0, imageSrc.length-9) + "true.png";
		document.getElementById("properties-table").classList.remove("hide-unspecified");
	}
}

function checkAll(bx)
{
	var cbs = document.getElementsByTagName('input');
	for(var i=0; i < cbs.length; i++)
	{
		if(cbs[i].type=='checkbox' && cbs[i].className=='probecheck')
		{
			cbs[i].checked = bx.checked;
		}
	}
}

function toggleStacktrace(image,more)
{
	var imageSrc = image.src;
	var moreElement = document.getElementById(more);
	if(imageSrc.substring(imageSrc.length-8)=="true.png")
	{
		image.src = imageSrc.substring(0, imageSrc.length-8) + "false.png";
		moreElement.style.display = "none";
	}
	else
	{
		image.src = imageSrc.substring(0, imageSrc.length-9) + "true.png";
		moreElement.style.display = "inline";
	}
}

function filterRows(text)
{
	var rows = document.body.querySelectorAll("tr.property");
	for(i=0; i<rows.length; i++)
	{
		var row = rows[i];
		var tdKey = row.getElementsByClassName("key");
		if (tdKey.item(0).textContent.includes(text))
			row.classList.remove("hidden-by-filter");
		else
			row.classList.add("hidden-by-filter");
	}
}
