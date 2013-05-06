/*
 * ====================================================
 * Copyright (C) 2013 by Idylwood Technologies, LLC. All rights reserved.
 *
 * Developed at Idylwood Technologies, LLC.
 * Permission to use, copy, modify, and distribute this
 * software is freely granted, provided that this notice 
 * is preserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * The License should have been distributed to you with the source tree.
 * If not, it can be found at
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Author: Charles Cooper
 * Date: 2013
 * ====================================================
 */

package com.idylwood.yahoo;

import java.util.EnumSet;

import com.idylwood.yahoo.Date;
import com.idylwood.yahoo.QuoteUrlBuilder.Tag;

// convenience class for getting quote data
// TODO make it work
class Quote {
	public final long time_accessed;
	public final String ticker;
	// public final String company_name;
	public final double bid;
	public final double ask;
	public final double dividend_yield;
	public final double dividend_per_share;
	public final Date dividend_pay_date;
	public final Date dividend_ex_date;
	public final double previous_close;
	public final double open;
	public final Date last_trade_date;
	public final double days_low;
	public final double days_high;
	public final double moving_average_200_day;
	public final double moving_average_50_day;
	public final double earnings_per_share;
	public final double eps_estimate_current_year;
	public final double eps_estimate_next_year;
	public final double eps_estimate_next_quarter;
	public final double book_value;
	public final double ebitda; // ebitda is tricky.
	public final double price_sales_ratio;
	public final double price_book_ratio;
	public final double price_earnings_ratio; // P/E ratio
	public final double short_ratio;
	public final double revenue;
	public final double shares_outstanding;
	public final long volume;
	public final long average_daily_volume;
	static final EnumSet<Tag> quoteTags =
			EnumSet.of(
					Tag.BID_REALTIME,
					Tag.ASK_REALTIME,
					Tag.DIVIDEND_YIELD,
					Tag.DIVIDEND_PER_SHARE,
					Tag.DIVIDEND_PAY_DATE,
					Tag.DIVIDEND_EX_DATE,
					Tag.PREVIOUS_CLOSE,
					Tag.OPEN,
					Tag.LAST_TRADE_DATE,
					Tag.DAYS_LOW,
					Tag.DAYS_HIGH,
					Tag.MOVING_AVERAGE_200_DAY,
					Tag.MOVING_AVERAGE_50_DAY,
					Tag.EARNINGS_PER_SHARE,
					Tag.EARNINGS_PER_SHARE_ESTIMATE_CURRENT_YEAR,
					Tag.EARNINGS_PER_SHARE_ESTIMATE_NEXT_YEAR,
					Tag.EARNINGS_PER_SHARE_ESTIMATE_NEXT_QUARTER,
					Tag.BOOK_VALUE,
					Tag.PRICE_SALES_RATIO,
					Tag.PRICE_BOOK_RATIO,
					Tag.PRICE_EARNINGS_RATIO,
					Tag.SHORT_RATIO,
					Tag.VOLUME,
					Tag.VOLUME_DAILY_AVERAGE,
					Tag.REVENUE,
					Tag.SHARES_OUTSTANDING,
					Tag.EBITDA
				  );
	Quote(final String symbol, final String[] tokens)
	{
		try
		{
			// this is like the worst invention ever
			time_accessed = System.currentTimeMillis();
			ticker = symbol;
			//company_name = tokens[0];
			bid = Double.parseDouble(tokens[idx(Tag.BID_REALTIME)]);
			ask = Double.parseDouble(tokens[idx(Tag.ASK_REALTIME)]);
			dividend_yield = Double.parseDouble(tokens[idx(Tag.DIVIDEND_YIELD)]);
			dividend_per_share = Double.parseDouble(tokens[idx(Tag.DIVIDEND_PER_SHARE)]);
			previous_close = Double.parseDouble(tokens[idx(Tag.PREVIOUS_CLOSE)]);
			open = Double.parseDouble(tokens[idx(Tag.OPEN)]);
			days_low = Double.parseDouble(tokens[idx(Tag.DAYS_LOW)]);
			days_high = Double.parseDouble(tokens[idx(Tag.DAYS_HIGH)]);
			moving_average_200_day = Double.parseDouble(tokens[idx(Tag.MOVING_AVERAGE_200_DAY)]);
			moving_average_50_day = Double.parseDouble(tokens[idx(Tag.MOVING_AVERAGE_50_DAY)]);
			earnings_per_share = Double.parseDouble(tokens[idx(Tag.EARNINGS_PER_SHARE)]);
			eps_estimate_current_year = Double.parseDouble(tokens[idx(Tag.EARNINGS_PER_SHARE_ESTIMATE_CURRENT_YEAR)]);
			eps_estimate_next_year = Double.parseDouble(tokens[idx(Tag.EARNINGS_PER_SHARE_ESTIMATE_NEXT_YEAR)]);
			eps_estimate_next_quarter = Double.parseDouble(tokens[idx(Tag.EARNINGS_PER_SHARE_ESTIMATE_NEXT_QUARTER)]);
			book_value = Double.parseDouble(tokens[idx(Tag.BOOK_VALUE)]);
			price_sales_ratio = Double.parseDouble(tokens[idx(Tag.PRICE_SALES_RATIO)]);
			price_book_ratio = Double.parseDouble(tokens[idx(Tag.PRICE_BOOK_RATIO)]);
			price_earnings_ratio = Double.parseDouble(tokens[idx(Tag.PRICE_EARNINGS_RATIO)]);
			short_ratio = Double.parseDouble(tokens[idx(Tag.SHORT_RATIO)]);
			revenue = Double.parseDouble(scientificNotation(tokens[idx(Tag.REVENUE)])); // 10.10B
			ebitda = Double.parseDouble(scientificNotation(tokens[idx(Tag.EBITDA)]));
			shares_outstanding = Long.parseLong(tokens[idx(Tag.SHARES_OUTSTANDING)].replaceAll(",| ","")); // 10,000,000
			volume = Long.parseLong(tokens[idx(Tag.VOLUME)]);
			average_daily_volume = Long.parseLong(tokens[idx(Tag.VOLUME_DAILY_AVERAGE)]);

			// dates are tricky so they get their own block
			final java.util.Date todaysDate = new java.util.Date(time_accessed);
			java.text.DateFormat df = new java.text.SimpleDateFormat("MMM dd"); // eg Jun 28
			java.util.Date parsed = df.parse(tokens[indexOf(quoteTags,Tag.DIVIDEND_PAY_DATE)]);
			parsed.setYear(todaysDate.getYear());
			dividend_pay_date = new Date(parsed);
			parsed = df.parse(tokens[indexOf(quoteTags,Tag.DIVIDEND_EX_DATE)]);
			parsed.setYear(todaysDate.getYear());
			dividend_ex_date = new Date(parsed);
			df = new java.text.SimpleDateFormat("mm/dd/yyyy");
			last_trade_date = new Date(df.parse(tokens[indexOf(quoteTags,Tag.LAST_TRADE_DATE)]));
		}
		catch (java.text.ParseException e)
		{
			throw new RuntimeException("You have bug!", e);
		}
	}

	// returns 10^9 for 'B', 10^6 for 'M', 10^3 for 'K' and 0 otherwise
	private static final long charToLong(final char c)
	{
		switch (c)
		{
		case 'B' : return 1000 * 1000 * 1000;
		//case 'b' : return 1000 * 1000 * 1000;
		case 'M' : return 1000 * 1000;
		//case 'm' : return 1000 * 1000;
		case 'K' : return 1000;
		//case 'k' : return 1000;
		default : return 0;
		}
	}
	// replace 'B'/'M'/'K' suffix with E9/E6 etc.
	private static String scientificNotation(final String s)
	{
		return s.replace("B","E9").replace("M","E6").replace("K","E3");
	}
	// to save typing
	private static final int idx(final Tag target)
	{
		return indexOf(quoteTags,target);
	}
	private static final int indexOf(final EnumSet<Tag> set, final Tag target)
	{
		int i = 0;
		for (Tag t : set)
		{
			if (t.equals(target))
				return i;
			i++;
		}
		return -1;
	}

}
