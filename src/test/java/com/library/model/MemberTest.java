package com.library.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class MemberTest {

    @Test
    void constructor_withValidData_storesValues() {
        Member member = new Member("A-1", "Jan", "Kowalski", "kowalskijan@gmail.com");

        assertEquals("A-1", member.id());
        assertEquals("Jan", member.firstName());
        assertEquals("Kowalski", member.lastName());
        assertEquals("kowalskijan@gmail.com", member.email());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t"})
    void constructor_whenIdIsBlank_throwsIllegalArgumentException(String id) {
        assertThrows(IllegalArgumentException.class,
                () -> new Member(id, "Jan", "Kowalski", "kowalskijan@gmail.com"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t"})
    void constructor_whenFirstNameIsBlank_throwsIllegalArgumentException(String firstName) {
        assertThrows(IllegalArgumentException.class,
                () -> new Member("A-1", firstName, "Kowalski", "kowalskijan@gmail.com"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t"})
    void constructor_whenLastNameIsBlank_throwsIllegalArgumentException(String lastName) {
        assertThrows(IllegalArgumentException.class,
                () -> new Member("A-1", "Jan", lastName, "kowalskijan@gmail.com"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t"})
    void constructor_whenEmailIsBlank_throwsIllegalArgumentException(String email) {
        assertThrows(IllegalArgumentException.class,
                () -> new Member("A-1", "Jan", "Kowalski", email));
    }

    @Test
    void constructor_whenEmailHasNoAt_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Member("A-1", "Jan", "Kowalski", "kowalskijangmail.com"));
    }

    @Test
    void constructor_whenTextHasSurroundingSpaces_stripsThem() {
        Member member =
                new Member("  A-1  ", "  Jan  ", "  Kowalski  ", "  kowalskijan@gmail.com  ");

        assertEquals("A-1", member.id());
        assertEquals("Jan", member.firstName());
        assertEquals("Kowalski", member.lastName());
        assertEquals("kowalskijan@gmail.com", member.email());
    }

    @Test
    void equals_whenMembersHaveSameData_returnsTrue() {
        Member member1 = new Member("A-1", "Jan", "Kowalski", "kowalskijan@gmail.com");
        Member member2 = new Member("A-1", "Jan", "Kowalski", "kowalskijan@gmail.com");

        assertEquals(member1, member2);
    }

    @Test
    void equals_whenMembersHaveDifferentId_returnsFalse() {
        Member member1 = new Member("A-1", "Jan", "Kowalski", "kowalskijan@gmail.com");
        Member member2 = new Member("A-2", "Jan", "Kowalski", "kowalskijan@gmail.com");

        assertNotEquals(member1, member2);
    }
}