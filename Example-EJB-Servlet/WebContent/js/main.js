function Point(city,lat,lon,numOfTweets){
	this.city=city;
	this.lat=lat;
	this.lon=lon;
	this.numOfTweets=numOfTweets;
}

var markers;
var bounds = new GLatLngBounds();


function loadData() {
	var para="";
	if(document.getElementById("checkKeyword").checked==false 
		&& document.getElementById("checkLocation").checked==false){
		alert("Please choose at least one of the following search options.");
		return;
	}else{
		var value=null;
		if(document.getElementById("checkKeyword").checked==true){
			if((value=document.getElementById("keywordList").value)!=""){
				para+="keywords="+value;
			}
		} 
		if(document.getElementById("checkLocation").checked==true){
			
			if((value=document.getElementById("locationList").value)!=""){
				//alert(value);
				if(para!=""){
					para+="&locations="+value;
				}else{
					para="locations="+value;
				}
			}
		}
	}
	if(para==""){
		alert("Please fill in at least one of the following search text area.");
	}
	//alert(para);
	/* <root>
	 * 		<state name="">
	 * 			<location geoId="" lat="" lon="">
	 * 				<tweet></tweet>
	 * 			</location>
	 * 		</state>
	 * </root>*/
	GDownloadUrl("SearchServlet?"+para, function(data, responseCode) {
		  // To ensure against HTTP errors that result in null or bad data,
		  // always check status code is equal to 200 before processing the
			// data
		/*state		city	# of tweets 	first 5 tweets*/
			map.clearOverlays();
		  if(responseCode == 200) {
		    var xml = GXml.parse(data);
		    var resultTable=document.createElement("table");
		    resultTable.id="theOnlyTable";
		    document.getElementById("resultContainer").innerHTML="";
		    document.getElementById("resultContainer").appendChild(resultTable);
		    var tbody=document.createElement("tbody");
		    //resultTable.style.height=document.getElementById("resultContainer").clientHeight;
		    resultTable.appendChild(tbody);
		    var tr=document.createElement("tr");
		    tbody.appendChild(tr);
		    var th=document.createElement("th");
		    th.style.width="40px";
		    th.innerHTML="State";
		    tr.appendChild(th);
		    
		    th=document.createElement("th");
		    th.style.width="40px";
		    th.innerHTML="City";
		    tr.appendChild(th);
		    
		    th=document.createElement("th");
		    th.style.width="50px";
		    th.innerHTML="#OfTweets";
		    tr.appendChild(th);
		    
		    th=document.createElement("th");
		    th.style.width=document.getElementById("resultContainer").width-130;
		    th.innerHTML="DetailedTweets";
		    tr.appendChild(th);
		    
		    var states = xml.documentElement.getElementsByTagName("state");
		    //alert(states.length);
		    if(states==null){
		    	alert("No tweets retrieved!");
		    	return;
		    }
		    var points=[];
		    for (var i = 0; i < states.length; i++) {
		    	var locations=states[i].getElementsByTagName("location");
		    	for(var j=0;j<locations.length;j++){
		    		var tweets=locations[j].getElementsByTagName("tweet");
		    		var tweetTexts=[];
		    		for(var k=0;k<tweets.length;k++){
		    			tweetTexts.push((k+1).toString()+"."+tweets[k].textContent);
		    		}
		    		points.push(new Point(locations[j].getAttribute("city"),
		    				locations[j].getAttribute("lat"), 
		    				locations[j].getAttribute("lon"), 
		    				tweets.length));
		    		//update result table
		    		
		    		if(tweetTexts.length>100){
		    			var tr=document.createElement("tr");
		    			var td=document.createElement("td");
		    			td.innerHTML=states[i].getAttribute("name");
		    			tr.appendChild(td);

		    			td=document.createElement("td");
		    			td.innerHTML=locations[j].getAttribute("city");
		    			tr.appendChild(td);
		    			
		    			td=document.createElement("td");
		    			td.innerHTML=tweets.length;
		    			tr.appendChild(td);

		    			td=document.createElement("td");
		    			var div=document.createElement("div");
		    			div.style.overflow="scroll";
		    			//div.style.height="200px";
		    			div.innerHTML=tweetTexts.slice(0,100).join("<br/>");
		    			td.appendChild(div);
		    			tr.appendChild(td);
		    		}else{
		    			var tr=document.createElement("tr");
		    			var td=document.createElement("td");
		    			td.innerHTML=states[i].getAttribute("name");
		    			tr.appendChild(td);

		    			td=document.createElement("td");
		    			td.innerHTML=locations[j].getAttribute("city");
		    			tr.appendChild(td);
		    			
		    			td=document.createElement("td");
		    			td.innerHTML=tweets.length;
		    			tr.appendChild(td);

		    			td=document.createElement("td");
		    			var div=document.createElement("div");
		    			div.style.overflow="auto";
		    			div.margin="0px";
		    			div.innerHTML=tweetTexts.join("<br/>");
		    			td.appendChild(div);
		    			tr.appendChild(td);
		    		}
		    		tbody.appendChild(tr);
		    	}
		    }
		    //alert(resultTable.innerHTML);

		    //points are sorted 
		    //points.sort(compare);
		    var maxNumOfTweets=0,sumNumOfTweets=0;
		    for(var i=0;i<points.length;i++){
		    	//alert(points[i].city);
		    	sumNumOfTweets+=points[i].numOfTweets;
		    	if(points[i].numOfTweets>maxNumOfTweets){
			    	maxNumOfTweets=points[i].numOfTweets;
		    	}
		    }
		    //alert(sumNumOfTweets);
		    var tweetPercentages=[];
		    for(var i=0;i<points.length;i++){
		    	//alert(points[i].city);
		    	tweetPercentages.push(points[i].numOfTweets*1.0/sumNumOfTweets);
		    }
		    //alert(tweetPercentages.join(","));

		    for(var i=0;i<points.length;i++){
		    	var point=points[i];
		    	var latlon = new GLatLng(parseFloat(point.lat),parseFloat(point.lon));
				bounds.extend(latlon);
				var html = "<div class='info'>"+point.city+"<br/>"
					+point.lat+ "," +point.lat
					+ "<br/># of tweets:"+ point.numOfTweets+"</div>";
		        var title=point.city;
		        var ratio=point.numOfTweets*1.0/maxNumOfTweets;
			    //alert(latlon.lat()+" "+latlon.lng());
			    //exit(0);
		        var marker = createMarker(latlon, html, title, createIcon(ratio));
				map.addOverlay(marker);
		    }
			map.setCenter(bounds.getCenter(),map.getBoundsZoomLevel(bounds));
			
	    	adjustTable(document.getElementById("theOnlyTable"),tweetPercentages);
		  } else if(responseCode == -1) {
		    alert("Data request timed out. Please try later.");
		  } else { 
		    alert("No results returned.");
		    document.getElementById("resultContainer").innerHTML="No results yet...";
		  }
		});
}

function adjustTable(thisTable,tweetPercentages){
	//alert(tweetPercentages.length);
	var resultContainer=document.getElementById("resultContainer");
	var minHeight=1000;
	var trs=thisTable.getElementsByTagName("tbody")[0].getElementsByTagName("tr");
	for(var i=1;i<trs.length;i++){
		if(trs[i].offsetHeight<minHeight){
			minHeight=trs[i].offsetHeight;
		}
	}

	//alert(thisTable.offsetHeight+","+trs.length);
	//trs=trs.splice(1,trs.length);
	//alert(minHeight);
	for(var i=1;i<trs.length;i++){
		//alert(trs[i].style.height);
		var div=trs[i].getElementsByTagName("td")[3].getElementsByTagName("div")[0];
		div.style.height=(resultContainer.clientHeight-trs[0].offsetHeight-minHeight-80)*tweetPercentages[i-1]+minHeight;
		//alert(tweetPercentages[i-1]);
		//alert(trs[i].style.height+","+trs[i].offsetHeight+","+trs[i].clientHeight);
		
	}
}
function createMarker(latlon, html, title, icon)
{
    var marker = new GMarker(latlon, {
        title: title,
        icon: icon
    });
    GEvent.addListener(marker, 'click', function ()
    {
        this.openInfoWindowHtml(html);
    });
	return marker;
}
 
function createIcon(ratio)
{
	//alert("ratio is "+ratio);
	var maxRadius=50;
	var minRadius=10;
    var icon = new GIcon();
    icon.image = "img/circle.png";
	//default size 20x34
    icon.iconSize = new GSize((maxRadius-minRadius)*ratio+minRadius, (maxRadius-minRadius)*ratio+minRadius);
    icon.iconAnchor = new GPoint((maxRadius-minRadius)*1.0/2, (maxRadius-minRadius)*1.0/2);
    icon.infoWindowAnchor = new GPoint((maxRadius-minRadius)*1.0/2, (maxRadius-minRadius)*1.0/2);
    icon.infoShadowAnchor = new GPoint((maxRadius-minRadius)*1.0/2, (maxRadius-minRadius)*1.0/2);
    return icon;
}

function compare(a,b) {
	if (a.numOfTweets < b.numOfTweets)
		return -1;
	if (a.numOfTweets > b.numOfTweets)
		return 1;
	return 0;
}
var map;
function init(){
	adjustWindow();
	map = new GMap2(document.getElementById("map"));
	var columbus=new GLatLng(39.966596, -83.009377);
    map.setCenter(columbus,10);
	map.setMapType(G_PHYSICAL_MAP);
	map.addControl(new GMenuMapTypeControl()); 
	map.addControl(new GScaleControl()); 
	map.addControl(new GOverviewMapControl());
	map.enableScrollWheelZoom();
	map.enableGoogleBar();
	map.openInfoWindowHtml(columbus, "<div class='info'>Hello world~</div>");
	map.addControl(new GSmallMapControl()); 
}
function adjustWindow(){
	var heighCompressRatio=0.1;
	var widthCompressRatio=0.07;
	var maxHeight = document.body.clientHeight*(1-heighCompressRatio);
	var maxWidth = document.body.clientWidth*(1-widthCompressRatio);
	var dividentRatio=0.4;
	//alert(maxHeight+" "+maxWidth);
	var divLeftPanel = document.getElementById('leftPanel');
	var divMap = document.getElementById('map');
//	/var marginLeft=10;
	divLeftPanel.style.width = maxWidth * dividentRatio;
	divLeftPanel.style.height=maxHeight;
	//divRightPanel.style.marginLeft=maxWidth * dividentRatio+marginLeft;
	divMap.style.height = maxHeight-document.getElementById("inputContainer").clientHeight;
	divMap.style.width = document.getElementById("leftPanel").offsetWidth-10;
	
	if(navigator.userAgent.search('Firefox')!=-1){

	}else if(navigator.userAgent.search('Safari')!=-1 || navigator.userAgent.search('Chrome')!=-1){

	}else if(navigator.userAgent.search('MSIE')!=-1){

	}   
	var resultContainer=document.getElementById('resultContainer');
	//alert(maxWidth+","+divLeftPanel.style.width);
	resultContainer.style.width=maxWidth-parseInt(divLeftPanel.style.width);
	resultContainer.style.height=maxHeight;
	//alert(resultContainer.style.width+","+resultContainer.style.height);
}


