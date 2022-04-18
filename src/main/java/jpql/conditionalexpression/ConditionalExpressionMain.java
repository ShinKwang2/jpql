package jpql.conditionalexpression;

import jpql.Member;
import jpql.MemberType;
import jpql.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class ConditionalExpressionMain {
    /**
     * 조건식
     *  - 기본 CASE 식
     *  select
     *      case when m.age <= 10 then '학생요금'
     *           when m.age >= 60 then '경로요금'
     *           else '일반 요금'
     *      end
     *  from Member m
     *
     *  - 단순 CASE 식
     *  select
     *      case t.name
     *          when '팀A' then '인센티브110%'
     *          when '팀B' then '인센티브120%'
     *          else '인센티브105%'
     *      end
     *   from Team t
     */
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("관리자");
            member.setAge(10);
            member.setType(MemberType.ADMIN);

            member.changeTeam(team);

            em.persist(member);

            em.flush();
            em.clear();

            String query = "select " +
                                "case when m.age <= 10 then '학생 요금'" +
                                "     when m.age >= 60 then '일반 요금'" +
                                "     else '일반요금' end " +
                            "from Member m";
            List<String> resultList = em.createQuery(query, String.class)
                    .getResultList();

            for (String s : resultList) {
                System.out.println("s = " + s);
            }

            /**
             * COALESCE : 하나씩 조회해서 null 이 아니면 반환
             * - select coalesce(m.username, '이름 없는 회원') from Member m
             */
            String queryCoalesce = "select coalesce(m.username, '이름 없는 회원') from Member m ";
            List<String> coalesceResultList = em.createQuery(queryCoalesce, String.class)
                    .getResultList();
            for (String s : coalesceResultList) {
                System.out.println("s = " + s);
            }

            /**
             * NULLIF : 두 값이 같으면 null 반환, 다르면 첫 번째 값 반환 - 주로 숨길 때 사용
             * - select NULLIF(m.username, '관리자') from Member m
             */
            String queryNullIf = "select nullif(m.username, '관리자') from Member m ";
            List<String> nullIfResultList = em.createQuery(queryNullIf, String.class)
                    .getResultList();
            for (String s : nullIfResultList) {
                System.out.println("s = " + s);
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();

    }
}
