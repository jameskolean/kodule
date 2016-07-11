package com.codegreenllc.kodule;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.sql.DataSource;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.eventbus.EventBus;

@SpringBootApplication
public class Application {

	public static ConfigurableApplicationContext context;

	public static void main(final String[] args) {
		context = SpringApplication.run(Application.class, args);
	}

	@Autowired
	DataSource dataSource;

	@Autowired
	DataSource liquibaseDataSource;

	// con't use @Loggable in config
	Logger log = LoggerFactory.getLogger(Application.class);

	@Bean
	public EventBus eventBus() {
		return new EventBus();
	}

	@Bean
	public Jackson2ObjectMapperBuilder jacksonBuilder() {
		final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
		builder.indentOutput(false);
		builder.deserializerByType(DateTimeZone.class, jsonDateTimeZoneDeserializer());
		builder.deserializerByType(DateTime.class, jsonDateTimeDeserializer());
		builder.deserializerByType(LocalDate.class, jsonLocalDateDeserializer());
		builder.serializerByType(DateTimeZone.class, jsonDateTimeZoneSerializer());
		builder.serializerByType(DateTime.class, jsonDateTimeSerializer());
		builder.serializerByType(LocalDate.class, jsonLocalDateSerializer());
		return builder;
	}

	@Bean(name = "jsonDateTimeDeserializer")
	public JsonDeserializer<DateTime> jsonDateTimeDeserializer() {
		return new JsonDeserializer<DateTime>() {

			@Override
			public DateTime deserialize(final JsonParser parser, final DeserializationContext context)
					throws IOException, JsonProcessingException {
				return DateTime.parse(parser.getValueAsString());
			}
		};
	}

	@Bean(name = "jsonDateTimeSerializer")
	public JsonSerializer<DateTime> jsonDateTimeSerializer() {
		return new JsonSerializer<DateTime>() {

			@Override
			public void serialize(final DateTime value, final JsonGenerator jgen, final SerializerProvider provider)
					throws IOException, JsonProcessingException {
				jgen.writeString(value.toString());
			}
		};
	}

	@Bean(name = "jsonDateTimeZoneDeserializer")
	public JsonDeserializer<DateTimeZone> jsonDateTimeZoneDeserializer() {
		return new JsonDeserializer<DateTimeZone>() {

			@Override
			public DateTimeZone deserialize(final JsonParser parser, final DeserializationContext context)
					throws IOException, JsonProcessingException {
				return DateTimeZone.forID(parser.getValueAsString());
			}
		};
	}

	@Bean(name = "jsonDateTimeZoneSerializer")
	public JsonSerializer<DateTimeZone> jsonDateTimeZoneSerializer() {
		return new JsonSerializer<DateTimeZone>() {

			@Override
			public void serialize(final DateTimeZone value, final JsonGenerator jgen, final SerializerProvider provider)
					throws IOException, JsonProcessingException {
				jgen.writeString(value.getID());
			}
		};
	}

	@Bean(name = "jsonLocalDateDeserializer")
	public JsonDeserializer<LocalDate> jsonLocalDateDeserializer() {
		return new JsonDeserializer<LocalDate>() {

			@Override
			public LocalDate deserialize(final JsonParser parser, final DeserializationContext context)
					throws IOException, JsonProcessingException {
				return LocalDate.parse(parser.getValueAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			}
		};
	}

	@Bean(name = "jsonLocalDateSerializer")
	public JsonSerializer<LocalDate> jsonLocalDateSerializer() {
		return new JsonSerializer<LocalDate>() {

			@Override
			public void serialize(final LocalDate value, final JsonGenerator jgen, final SerializerProvider provider)
					throws IOException, JsonProcessingException {
				jgen.writeString(value.toString());
			}
		};
	}

}
