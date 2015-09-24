package com.example.footspotretrofit.rx_bus;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;

public class RxBus<T> {
	private final Subject<T, T> subject;

	public RxBus() {
		this(PublishSubject.<T>create());
	}

	public RxBus(Subject<T, T> subject) {
		this.subject = subject;
	}

	public <E extends T> void send(E event) {
		subject.onNext(event);
	}

	public  Subject<T, T> toSubject() {
		return subject;
	}

	public Observable<T> toObserverable() {
		return subject;
	}

	public <E extends T> Observable<E> observeEvents(Class<E> eventClass) {
		return subject.ofType(eventClass);//pass only events of specified type, filter all other
	}
}


