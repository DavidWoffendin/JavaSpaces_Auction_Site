# tuple-spaces-auction-room

This repo contains a University assignment based around the use of a tuple space (in this case JavaSpaces), in order to create an auction room simulation. I thought I'd share it as a **point of reference** for future students :)

The following are the main requirements of this application:

1. The ability for anyone in the room to add a new lot for auction. This can simply be a text description, although some notes of the seller's details will be needed.
2. The ability for anyone in the room to scroll through all available lots in the space and to see details of the lot, including a bid history.
3. The ability for anyone in the room to make either public or private bids against any item. Public bids will be visible to anyone looking at a particular lot but private bids can be seen only by the person selling the item or by the person who placed the bid.
4. The ability for the owner of a lot to withdraw the item from the sale at any time or to accept the current highest bid. The winner (if there is one) should be informed via the JavaSpace.

### How To Build

This project is built using Maven;

```
# tests require a local space
mvn clean install -DskipTests=true
```

### How To Run

Simply start up the app, connecting to your space:

```
java -jar -Djava.security.policy=./policy.all target/javaspaces-1.0-SNAPSHOT.jar localhost
```
	

