package com.library.repository;

import com.library.model.Member;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryMemberRepository implements MemberRepository {

    Map<String, Member> members = new LinkedHashMap<>();

    @Override
    public void save(Member member) {
        members.put(member.id(), member);
    }

    @Override
    public Optional<Member> findById(String id) {
        return Optional.of(members.get(id));
    }

    @Override
    public boolean existsById(String id) {
        return members.containsKey(id);
    }

    @Override
    public List<Member> findAll() {
        return List.copyOf(members.values());
    }

}
