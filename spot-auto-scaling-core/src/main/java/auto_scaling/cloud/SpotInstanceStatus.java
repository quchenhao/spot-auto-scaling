package auto_scaling.cloud;

import java.util.Date;

public class SpotInstanceStatus extends InstanceStatus{
	
	protected double biddingPrice;
	protected String spotRequestId;

	public SpotInstanceStatus(String id, String spotRequestId, String publicUrl, String privateUrl, Date launchTime, InstanceTemplate type, double biddingPrice) {
		super(id, publicUrl, privateUrl, launchTime, type);
		this.biddingPrice = biddingPrice;
		this.spotRequestId = spotRequestId;
	}

	public double getBiddingPrice() {
		return biddingPrice;
	}

	public String getSpotRequestId() {
		return spotRequestId;
	}
	
	public int hashCode() {
		return (id + spotRequestId).hashCode();
	}
	
	@Override
	public String toString() {
		return super.toString() + " spot_request_id: " + spotRequestId; 
	}
	
}
