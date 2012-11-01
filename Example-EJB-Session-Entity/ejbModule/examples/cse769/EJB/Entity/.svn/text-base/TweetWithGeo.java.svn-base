package examples.cse769.EJB.Entity;

import java.io.Serializable;
import javax.persistence.*;


@Entity
@Table(name="TWEET_GEO_VIEW")


public class TweetWithGeo implements Serializable{
	private static final long serialVersionUID = 1L;
	private String id;
	private int geo_id;
	private String created_at;
	private String from_user_id;
	private String to_user_id;
	private String from_user;
	private String profile_image_url;
	private String iso_language_code;
	private String source;
	private String text;
	
	private double lat;
	private double lon;
	private String stusps ;
	private String stname;
	private String city;
	
	
	
	@Column(name="geo_id")
	public int getGeo_id(){
		return this.geo_id;
	}
	
	public void setGeo_id(int geo){
		this.geo_id = geo;
	}
	
	//primary key of each tweet

	@Id
	@Column(name="id")
	public String getId(){
		return this.id;
	}
	
	public void setId(String id){
		this.id=id;
	}
	
	public void setCreated_at(String created_at){
		this.created_at=created_at;
	}
	public void setFrom_user_id(String from_user_id){
		this.from_user_id=from_user_id;
	}
	public void setTo_user_id(String to_user_id){
		this.to_user_id=to_user_id;
	}
	public void setFrom_user(String from_user){
		this.from_user=from_user;
	}
	public void setProfile_image_url(String profile_image_url){
		this.profile_image_url=profile_image_url;
	}
	public void setIso_language_code(String iso_language_code){
		this.iso_language_code=iso_language_code;
	}

	public void setSource(String source){
		this.source=source;
	}
	public void setText(String text){
		this.text=text;
	}
	public void setStusps(String stusps){
		this.stusps = stusps;
	}
	public void setStname(String stname){
		this.stname=stname;
	}
	public void setLat(double lat){
		this.lat=lat;
	}
	public void setLon(double lon){
		this.lon=lon;
	}
	public void setCity(String city){
		this.city=city;
	}
	
	//getters
	@Column(name="created_at")
	public String getCreated_at(){
		return this.created_at;
	}
	@Column(name="from_user_id")
	public String getFrom_user_id(){
		return this.from_user_id;
	}
	@Column(name="to_user_id")
	public String getTo_user_id(){
		return this.to_user_id;
	}
	@Column(name="from_user")
	public String getFrom_user(){
		return this.from_user;
	}
	@Column(name="profile_image_url")
	public String getProfile_image_url(){
		return this.profile_image_url;
	}
	@Column(name="iso_language_code")
	public String getIso_language_code(){
		return this.iso_language_code;
	}

	@Column(name="source")	
	public String getSource(){
		return this.source;
	}

	@Column(name="text")
	public String getText(){
		return this.text;
	}
	
	@Column(name="lat")
	public double getLat(){
		return this.lat;
	}
	
	@Column(name="lon")
	public double getLon(){
		return this.lon;
	}
	
	@Column(name="stusps", columnDefinition = "char(2)")
	public String getStusps(){
		return this.stusps;
	}
	
	@Column(name="stname")
	public String getStname(){
		return this.stname;
	}
	
	@Column(name="city")
	public String getCity(){
		return this.city;
	}
	
	
	

}
