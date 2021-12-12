import { Component, ContentChildren, QueryList, Input, ViewChild, ElementRef, ChangeDetectorRef, AfterViewChecked, AfterViewInit, OnDestroy } from '@angular/core';
import { style, animate, AnimationPlayer, AnimationBuilder, AnimationFactory } from '@angular/animations';


@Component({
	selector: 'carousel-item',
	templateUrl: './carousel-item.component.html',
	styleUrls: ['./carousel-item.component.css']
})
export class CarouselItem implements AfterViewChecked {
	width = 100;
	height = 100;

	constructor(public elem: ElementRef, private cdRef: ChangeDetectorRef) {
	}

	setWidth(newWidth) {
		this.width = newWidth;
	}

	setHeight(newHeight) {
		this.height = newHeight;
	}

	ngAfterViewChecked() {
		this.cdRef.detectChanges();
	}

}

@Component({
	selector: 'carousel',
	templateUrl: './carousel.component.html',
	styleUrls: ['./carousel.component.css']
})
export class CarouselComponent implements AfterViewInit, OnDestroy {

	@ContentChildren(CarouselItem) items: QueryList<CarouselItem>;
	@Input() width: String = undefined;
	@Input() height: String = undefined;
	@Input() automatic: number = undefined;
	@ViewChild('carousel', { static: true }) private carousel: ElementRef;

	timing = '250ms ease-in';
	private player: AnimationPlayer;
	private currentSlide = 0;
	private interval = undefined;

	constructor(private builder: AnimationBuilder) {
	}

	/**
	 * Calculate the input width and height to reflect the carousel style 
	 * @param width 
	 * @param height 
	 */
	calculateSize(width, height) {
		let style = {};
		let units = ["cm", "mm", "in", "px", "pt", "pc", "em", "ex", "ch", "rem", "vw", "vh", "vmin", "vmax", "%"];
		if (width) {
			width = width.toString();
			let widthUnit = width.replace(/\d/g, '');
			let widthValue = width.replace(/\D/g, '');

			if (units.indexOf(widthUnit) > -1) {
				style['width.'+widthUnit] = widthValue;
			} else {
				style['width.%'] = '100';
			}
		} else {
			style['width.%'] = '100';
		}
		if (height) {
			height = height.toString();
			let heightUnit = height.replace(/\d/g, '');
			let heightValue = height.replace(/\D/g, '');

			if (units.indexOf(heightUnit) > -1) {
				style['height.'+heightUnit] = heightValue;
			} else {
				style['height.%'] = '100';
			}
		} else {
			style['height.%'] = '100';
		}
		return style;
	}

	ngAfterViewInit() {
		// updates the width and height of the carousel items
		this.items.forEach((child) => {
			if (this.carousel.nativeElement.offsetWidth != child.width) {
				child.setWidth(this.carousel.nativeElement.offsetWidth);
				child.ngAfterViewChecked();
			}
			if (this.carousel.nativeElement.offsetHeight != child.height) {
				child.setHeight(this.carousel.nativeElement.offsetHeight);
				child.ngAfterViewChecked();
			}
		});
		if (this.automatic !== undefined && !isNaN(Number(this.automatic))) {
			this.enableAutomatic(this.automatic);
		}
	}

	ngOnDestroy() {
		if (this.interval !== undefined) {
			clearInterval(this.interval);
		}
	}

	private buildAnimation(offset) {
		return this.builder.build([
			animate(this.timing, style({ transform: `translateX(-${offset}px)` }))
		]);
	}

	next() {
		if (this.currentSlide + 1 === this.items.length) return;
		this.currentSlide = (this.currentSlide + 1) % this.items.length;
		const offset = this.currentSlide * this.carousel.nativeElement.offsetWidth;
		const myAnimation: AnimationFactory = this.buildAnimation(offset);
		this.player = myAnimation.create(this.carousel.nativeElement);
		this.player.play();
	}

	prev() {
		if (this.currentSlide === 0) return;

		this.currentSlide = ((this.currentSlide - 1) + this.items.length) % this.items.length;
		const offset = this.currentSlide * this.carousel.nativeElement.offsetWidth;

		const myAnimation: AnimationFactory = this.buildAnimation(offset);
		this.player = myAnimation.create(this.carousel.nativeElement);
		this.player.play();
	}

	/**
	 * move to a new slide
	 * @param i 
	 */
	moveToSlide(i: number) {
		if (this.currentSlide !== i) {
			this.currentSlide = i;
			const offset = this.currentSlide * this.carousel.nativeElement.offsetWidth;
			const myAnimation: AnimationFactory = this.buildAnimation(offset);
			this.player = myAnimation.create(this.carousel.nativeElement);
			this.player.play();
		}
	}

	/**
	 * Make sure to only run this after view is intialized to fill the items value
	 * @param seconds 
	 */
	enableAutomatic(seconds: number) {
		this.interval = setInterval(() => {
			this.moveToSlide((this.currentSlide + 1) % this.items.length)
		}, seconds * 1000);
	}

	/**
	 * Responsive Design
	 * @param event 
	 */
	onResize($event: any) {
		this.items.forEach((child) => {
			if (this.carousel.nativeElement.offsetWidth != child.width) {
				child.setWidth(this.carousel.nativeElement.offsetWidth);
				child.ngAfterViewChecked();
			}
			if (this.carousel.nativeElement.offsetHeight != child.height) {
				child.setHeight(this.carousel.nativeElement.offsetHeight);
				child.ngAfterViewChecked();
			}
		});
		this.moveToSlide(0);
	}

}

