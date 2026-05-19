# Solo Adventure Maze
> ITM413 Computer Language — Final Project

텍스트 기반 어드벤처 게임. 히어로가 여러 방을 탐험하며 몬스터를 처치하고 열쇠를 얻어 탈출한다.

A text-based dungeon adventure game where a hero navigates rooms, fights monsters, collects items, and escapes through the master door.

---

## 팀 / Team

| 이름 | 역할 |
|------|------|
| 오경빈(Oh Gyeong-bin) | Hero, Monster, Weapon, Item 계층 설계 |
| 염성민(Yeom Seong-min) | Room, Door, main method 구현 |

---

## 게임 방법 / How to Play

```
w   위로 이동    move up
a   왼쪽 이동   move left
s   아래 이동   move down
d   오른쪽 이동  move right
q   게임 종료   quit
```

몬스터에 인접하면 전투 메뉴가 표시됩니다.
When adjacent to a monster, a combat menu appears.

---

```
프로젝트 루트/
├── rooms/
│   ├── start.csv     ← 필수
│   ├── room2.csv
│   └── ...
└── src/
```

## 클래스 구조 / Class Structure

```
Entity (abstract)
  ├── Hero                implements Combat
  ├── Monster (abstract)  implements Combat
  │     ├── Goblin
  │     ├── Orc
  │     └── Troll
  └── Item (abstract)
        ├── Weapon (abstract)
        │     ├── Stick
        │     ├── WeakSword
        │     └── StrongSword
        └── HealingPotion (abstract)
              ├── MinorFlask
              └── BigFlask

Combat   (interface) — attack, takeDamage, combatWith
Room     — CSV 파싱, 그리드 관리, 이동 처리
Door     — 방 연결, 입장 위치 계산
```

---

## 전투 / Combat

- 무기 장착 시 인접 몬스터에게 자동 전투 진행
- 히어로와 몬스터가 동시에 데미지를 교환
- Troll 처치 시 열쇠 드롭 → 마스터 도어 해제

When armed, combat triggers automatically when adjacent to a monster.
Damage is exchanged simultaneously.
Defeating the Troll drops the key to unlock the master door.

---
