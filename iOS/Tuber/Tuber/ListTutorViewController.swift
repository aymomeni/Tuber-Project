//
//  ListTutorViewController.swift
//  Tuber
//
//  Created by Hyunjin Cho on 2017. 4. 6..
//  Copyright © 2017년 Tuber. All rights reserved.
//

import UIKit

class ListTutorViewController: UIViewController
{
    
    var tutorFNames: [String] = [];
    var tutorLNames: [String] = [];
    var tutorEmail: [String] = [];

    override func viewDidLoad()
    {
        super.viewDidLoad()        //prepTutorList();
    }
    
    override func viewDidAppear(_ animated: Bool)
    {}
    
    override func didReceiveMemoryWarning()
    {super.didReceiveMemoryWarning()}
}
