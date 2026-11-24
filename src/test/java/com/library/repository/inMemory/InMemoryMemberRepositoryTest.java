package com.library.repository.inMemory;

import com.library.model.Member;
import com.library.repository.MemberRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryMemberRepositoryTest {

    private final MemberRepository memberRepository = new InMemoryMemberRepository();
    private final Member member1 =
            new Member("A-1", "Jan", "Kowalski", "kowalski@gmail.com");
    private final Member member2 =
            new Member("A-2", "Anna", "Kowalska", "kowalska@gmail.com");
    private final Member member3 =
            new Member("A-1", "Anna", "Kowalska", "kowalska@gmail.com");

    @Test
    void save_whenSaveMemberCorrectly_findByIdShowsThisMember() {
        memberRepository.save(member1);

        assertEquals(Optional.of(member1), memberRepository.findById("A-1"));
    }

    @Test
    void save_whenMemberWithSameIdSaved_overwritesExistingMember() {
        memberRepository.save(member1);
        memberRepository.save(member3);

        assertEquals(List.of(member3), memberRepository.findAll());
        assertEquals(Optional.of(member3), memberRepository.findById("A-1"));
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
    void existsById_whenExists_returnsTrue() {
        memberRepository.save(member1);

        assertTrue(memberRepository.existsById("A-1"));
    }

    @Test
    void existsById_whenNotExists_returnsFalse() {
        assertFalse(memberRepository.existsById("A-1"));
    }

    @Test
    void findAll_whenNoMembers_returnsEmptyList() {
        assertEquals(List.of(), memberRepository.findAll());
    }

    @Test
    void findAll_whenMembersSaved_returnsThemInInsertionOrder() {
        memberRepository.save(member1);
        memberRepository.save(member2);

        assertEquals(List.of(member1, member2), memberRepository.findAll());
    }

    @Test
    void findAll_whenReturnedListIsModified_throwsUnsupportedOperationException() {
        memberRepository.save(member1);
        List<Member> members = memberRepository.findAll();

        assertThrows(UnsupportedOperationException.class, () -> members.add(member2));
        assertEquals(List.of(member1), memberRepository.findAll());
    }
}