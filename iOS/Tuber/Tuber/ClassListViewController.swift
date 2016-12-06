//
//  ClassListViewController.swift
//  Tuber
//
//  Created by Anne on 12/6/16.
//  Copyright © 2016 Tuber. All rights reserved.
//

import UIKit

class ClassListViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {

    @IBOutlet weak var classTableView: UITableView!
    
    var classes = ["CS 4400", "CS 3100", "CS 4150"]
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 3
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath) as! ClassTableViewCell
        
        cell.classNameLabel.text = classes[indexPath.row]
        
        return cell
    }
}
