package com.library.repository;

import com.library.model.Member;

import java.util.List;
import java.util.Optional;

/**
 * Storage for library members. Contains no business rules.
 */
public interface MemberRepository {

    /** Stores the member. Does not check for duplicates (the service does). */
    void save(Member member);

    /** Returns the member with the given id, or an empty Optional if there is none. */
    Optional<Member> findById(String id);

    /** Returns true if a member with the given id is stored. */
    boolean existsById(String id);

    /** Returns all stored members. Never returns null. */
    List<Member> findAll();

}