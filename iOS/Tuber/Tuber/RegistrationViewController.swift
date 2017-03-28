//
//  RegistrationViewController.swift
//  Tuber
//
//  Created by Hyunjin Cho on 2017. 1. 17..
//  Copyright © 2017년 Tuber. All rights reserved.
//

import UIKit

class RegistrationViewController: UIViewController {


    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var Fname: UITextField!
    @IBOutlet weak var Lname: UITextField!
    @IBOutlet weak var DOBpicker: UIDatePicker!
    @IBOutlet weak var password: UITextField!
    @IBOutlet weak var confirmPassword: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func nextButtonPressed(_ sender: Any) {
        let format = DateFormatter();
        format.dateFormat = "yyyy-MM-dd";
        var toPass = [String()]
        toPass.append(email.text!);
        toPass.append(Fname.text!);
        toPass.append(Lname.text!);
        toPass.append(password.text!);
        toPass.append(format.string(from:DOBpicker.date));
        performSegue(withIdentifier: "toSecondScreen", sender: toPass);
    }
    /*
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "toSecondScreen"
        {
            if let destination = segue.destination as? Registration2ViewController
            {
                destination.passedInfo = sender as! [String]
            }
            
        }
    }
     */
}
