package com.library.repository.jdbc;

import com.library.exception.StorageException;
import com.library.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcMemberRepositoryIT {

    private final TestDatabase testDatabase = new TestDatabase();
    private JdbcMemberRepository memberRepository;
    private final Member member1 =
            new Member("A-1", "Jan", "Kowalski", "kowalski@gmail.com");
    private final Member member2 =
            new Member("A-2", "Anna", "Nowak", "nowak@gmail.com");

    @BeforeEach
    void setUp() {
        testDatabase.cleanDatabase();
        memberRepository = new JdbcMemberRepository(testDatabase.connectionFactory());
    }

    @Test
    void save_whenNewMember_canBeReadBackById() {
        memberRepository.save(member1);

        assertEquals(Optional.of(member1), memberRepository.findById("A-1"));
    }

    @Test
    void save_whenIdAlreadyExists_throwsStorageException() {
        memberRepository.save(member1);
        Member member = new Member("A-1", "Kornel", "Ignacy", "ignacy@gmail.com");

        assertThrows(StorageException.class, () -> memberRepository.save(member));
        assertEquals(List.of(member1), memberRepository.findAll());
    }

    @Test
    void save_whenSeveralMembers_keepsThemSeparate() {
        memberRepository.save(member1);
        memberRepository.save(member2);

        assertEquals(Optional.of(member1), memberRepository.findById("A-1"));
        assertEquals(Optional.of(member2), memberRepository.findById("A-2"));
    }

    @Test
    void save_whenTextHasPolishCharacters_canBeReadBackUnchanged() {
        Member member = new Member("A-3", "Krzysztof", "Żółć", "zolc@gmail.com");
        memberRepository.save(member);

        assertEquals(Optional.of(member), memberRepository.findById("A-3"));
    }

    @Test
    void findById_whenIdUnknown_returnsEmptyOptional() {
        assertEquals(Optional.empty(), memberRepository.findById("A-1"));
    }

    @Test
    void findById_whenOtherMemberSaved_returnsEmptyOptional() {
        memberRepository.save(member1);

        assertEquals(Optional.empty(), memberRepository.findById("A-2"));
    }

    @Test
    void existsById_whenMemberSaved_returnsTrue() {
        memberRepository.save(member1);

        assertTrue(memberRepository.existsById("A-1"));
    }

    @Test
    void existsById_whenMemberNotSaved_returnsFalse() {
        assertFalse(memberRepository.existsById("A-2"));

        memberRepository.save(member1);

        assertFalse(memberRepository.existsById("A-2"));
    }

    @Test
    void findAll_whenNoMembers_returnsEmptyList() {
        assertEquals(List.of(), memberRepository.findAll());
    }

    @Test
    void findAll_whenMembersSaved_returnsThemOrderedById() {
        memberRepository.save(member2);
        memberRepository.save(member1);

        assertEquals(List.of(member1, member2), memberRepository.findAll());
    }

    @Test
    void findAll_whenReturnedListIsModified_throwsUnsupportedOperationException() {
        memberRepository.save(member1);

        assertThrows(UnsupportedOperationException.class, () -> memberRepository.findAll().add(member2));
        assertEquals(List.of(member1), memberRepository.findAll());
    }
}