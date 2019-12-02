package main

import (
	"fmt"
	"io/ioutil"
	"strconv"
	"strings"
)

func fuelRequirement(moduleMass int) int {
	fuel := moduleMass/3 - 2
	if fuel > 0 {
		return fuel
	} else {
		return 0
	}
}

func main() {
	input, err := ioutil.ReadFile("../input.txt")
	if err == nil {
		lines := strings.Split(string(input), "\n")
		totalFuel := 0
		for _, line := range lines {
			mass, _ := strconv.Atoi(line)
			additionalFuel := fuelRequirement(mass)
			for additionalFuel > 0 {
				totalFuel += additionalFuel
				additionalFuel = fuelRequirement(additionalFuel)
			}
		}
		fmt.Println("Total fuel required : ", totalFuel)
	}
}
