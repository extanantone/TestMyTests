package com.example.CalculadoraMetrosCuadrados.testmytests;

import com.example.CalculadoraMetrosCuadrados.dto.HouseResponseSquareFeetPerRoomDTO;
import com.example.CalculadoraMetrosCuadrados.dto.HouseResponseValueDTO;
import com.example.CalculadoraMetrosCuadrados.dto.RoomDTO;
import com.example.CalculadoraMetrosCuadrados.dto.RoomSquareFeetDTO;
import com.example.CalculadoraMetrosCuadrados.exception.NoSuchBarrioException;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;

abstract class TestOperation <T> implements BiFunction<Class<?>, Object, Object> {
    private Class<T> type;

    public TestOperation(Class<T> type) { this.type = type; }

    @Override
    public Object apply(Class<?> returnType, Object original) {
        if (  returnType == type ) {
            return makeWrong( (T) original );
        }
        return original;
    }

    protected abstract Object makeWrong(T spoil);
}


public class TestMyTests {
    public final String instance = System.getProperty("test_instance");

    public final static Map<String, Supplier<BiFunction<Class<?>, Object, Object>>> TEST_INSTANCES = new TreeMap<>();

    static {
        TEST_INSTANCES.put("biggestRoomWidthZero", () -> {
            return new TestOperation<RoomDTO>(RoomDTO.class) {
                @Override
                protected Object makeWrong(RoomDTO spoil) {
                    spoil.setRoom_width(0D);
                    return spoil;
                }
            };
        });

        TEST_INSTANCES.put("houseValuePlusOne", () -> {
            return new TestOperation<HouseResponseValueDTO>(HouseResponseValueDTO.class) {
                @Override
                protected Object makeWrong(HouseResponseValueDTO spoil) {
                    return new HouseResponseValueDTO(
                            spoil.getName(),
                            spoil.getTotalSquareFeet(),
                            spoil.getValue() + 1
                    );
                }
            };
        });

        TEST_INSTANCES.put("calculatePriceRuntimeException", () -> {
            return new TestOperation<HouseResponseValueDTO>(HouseResponseValueDTO.class) {
                @Override
                protected Object makeWrong(HouseResponseValueDTO spoil) {
                    throw new RuntimeException();
                }
            };
        });

        TEST_INSTANCES.put("removeOneRoom", () -> {
            return new TestOperation<HouseResponseSquareFeetPerRoomDTO>(HouseResponseSquareFeetPerRoomDTO.class) {
                @Override
                protected Object makeWrong(HouseResponseSquareFeetPerRoomDTO spoil) {
                    var rooms = spoil.getRoomsSquareFeet();
                    if (rooms.size() > 0) {
                        var room = rooms.get(0);
                        room = new RoomSquareFeetDTO(
                                room.getName(),
                                room.getSquareFeet() - 1 );

                        rooms.add(0, room);
                    }

                    return new HouseResponseSquareFeetPerRoomDTO(
                            rooms
                    );
                }
            };
        });
    }

    public Object operate(Class<?> returnType, Object original) {
        if(instance != null && TEST_INSTANCES.containsKey(instance)) {
            return TEST_INSTANCES
                    .get(instance)                  //Map.get
                    .get()                          //Supplier.get
                    .apply(returnType, original);   //BiFunction.apply
        }
        return original;
    }
}
