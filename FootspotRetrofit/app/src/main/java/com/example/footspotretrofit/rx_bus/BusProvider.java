package com.example.footspotretrofit.rx_bus;

public class BusProvider {
	private static RxBus<BusEvent> instance_;

	public static RxBus getBus() {
		if(instance_ == null){
			instance_ = new RxBus<>();
		}
		return instance_;
	}
}
