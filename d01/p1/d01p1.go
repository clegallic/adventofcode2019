package main

import (
	"fmt"
	"io/ioutil"
	"strconv"
	"strings"
)

func fuelRequirement(moduleMass int) int {
	return moduleMass/3 - 2
}

func main() {
	input, err := ioutil.ReadFile("../input.txt")
	if err == nil {
		lines := strings.Split(string(input), "\n")
		totalFuel := 0
		for _, line := range lines {
			mass, _ := strconv.Atoi(line)
			totalFuel += fuelRequirement(mass)
		}
		fmt.Println("Total fuel required : ", totalFuel)
	}
}
